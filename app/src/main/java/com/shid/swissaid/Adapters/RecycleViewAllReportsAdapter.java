package com.shid.swissaid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shid.swissaid.Model.Upload;
import com.shid.swissaid.Model.User;
import com.shid.swissaid.R;
import com.shid.swissaid.UI.AllReportActivity;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleViewAllReportsAdapter extends RecyclerView.Adapter<RecycleViewAllReportsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Upload> uploadArrayList;
    private String employee_name;
    private String mission;
    private String file_name;
    private String numero;
    private String date;

    private RecycleViewMyReportsAdapter.onRecyclerViewItemClickListener mItemClickListener;

    public void setOnItemClickListener(RecycleViewMyReportsAdapter.onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, int position);
    }

    public RecycleViewAllReportsAdapter(Context mContext) {
        this.mContext = mContext;
        this.uploadArrayList = new ArrayList<Upload>();
        this.uploadArrayList.addAll(AllReportActivity.uploadList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = new User();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot uniqueUserSnapshot : dataSnapshot.getChildren()) {
                    if (AllReportActivity.uploadList.get(position).getName_employee().equals(uniqueUserSnapshot.child("name").getValue().toString())){
                        user.setImageUrl(uniqueUserSnapshot.child("imageUrl").getValue().toString());
                        if (user.getImageUrl().equals("default")) {
                            holder.imageView.setImageResource(R.mipmap.icon);
                        } else {
                            Glide.with(mContext.getApplicationContext()).load(user.getImageUrl()).into(holder.imageView);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        employee_name = mContext.getString(R.string.textView_name);
        mission = mContext.getString(R.string.textView_mission);
        file_name = mContext.getString(R.string.textView_fileName);
        numero = mContext.getString(R.string.textView_numero);
        date = mContext.getString(R.string.textView_date);

        holder.employee_name.setText(employee_name + " " + AllReportActivity.uploadList.get(position).getName_employee());
        holder.file_name.setText(file_name + " " + AllReportActivity.uploadList.get(position).getName());
        holder.mission.setText(mission + " " + AllReportActivity.uploadList.get(position).getMission());
        holder.number.setText(numero + " " + AllReportActivity.uploadList.get(position).getNumero_ta());
        holder.date.setText(date + " " + AllReportActivity.uploadList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return AllReportActivity.uploadList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView imageView;
        TextView employee_name;
        TextView mission;
        TextView file_name;
        TextView number;
        TextView date;
        CardView parentLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_bis);
            employee_name = itemView.findViewById(R.id.name_text_view);
            mission = itemView.findViewById(R.id.mission_text_view);
            file_name = itemView.findViewById(R.id.file_name_text_view);
            number = itemView.findViewById(R.id.numero_ta_text_view);
            date = itemView.findViewById(R.id.time_text_view);
            parentLayout = itemView.findViewById(R.id.list_item_row_layout);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition());
            }
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        AllReportActivity.uploadList.clear();
        if (charText.length() == 0) {
            AllReportActivity.uploadList.addAll(uploadArrayList);
        } else {
            for (Upload wp : uploadArrayList) {
                if (wp.getMission().toLowerCase(Locale.getDefault()).contains(charText)) {
                    AllReportActivity.uploadList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filter_name(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        AllReportActivity.uploadList.clear();
        if (charText.length() == 0) {
            AllReportActivity.uploadList.addAll(uploadArrayList);
        } else {
            for (Upload wp : uploadArrayList) {
                if (wp.getName_employee().toLowerCase(Locale.getDefault()).contains(charText)) {
                    AllReportActivity.uploadList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filter_number(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        AllReportActivity.uploadList.clear();
        if (charText.length() == 0) {
            AllReportActivity.uploadList.addAll(uploadArrayList);
        } else {
            for (Upload wp : uploadArrayList) {
                if (wp.getNumero_ta().toLowerCase(Locale.getDefault()).contains(charText)) {
                    AllReportActivity.uploadList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filter_date(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        AllReportActivity.uploadList.clear();
        if (charText.length() == 0) {
            AllReportActivity.uploadList.addAll(uploadArrayList);
        } else {
            for (Upload wp : uploadArrayList) {
                if (wp.getTime().toLowerCase(Locale.getDefault()).contains(charText)) {
                    AllReportActivity.uploadList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }


}
