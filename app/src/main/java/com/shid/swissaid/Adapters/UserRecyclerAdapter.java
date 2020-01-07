package com.shid.swissaid.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shid.swissaid.Model.User;
import com.shid.swissaid.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {

    private ArrayList<User> mUsers = new ArrayList<>();
    private UserListRecyclerClickListener mClickListener;


    public UserRecyclerAdapter(ArrayList<User> users, UserListRecyclerClickListener clickListener) {

        this.mUsers = users;
        mClickListener = clickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, mClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        (holder).username.setText(mUsers.get(position).getUsername());
        (holder).email.setText(mUsers.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView username, email;
        UserListRecyclerClickListener mClickListener;

        public ViewHolder(View itemView, UserListRecyclerClickListener clickListener) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            mClickListener = clickListener;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mClickListener.OnUserClicked(getAdapterPosition());
        }
    }

    public interface UserListRecyclerClickListener {
        void OnUserClicked(int position);
    }

}



