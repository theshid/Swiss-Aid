package com.shid.swissaid.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;
import com.shid.swissaid.Model.Chat;
import com.shid.swissaid.Model.User;
import com.shid.swissaid.R;
import com.shid.swissaid.UI.MessageActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;
    String theLastMessage;
    String mediaMessage;
    String doc_name;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final User user = mUsers.get(position);
        viewHolder.username.setText(user.getUsername());
        if (user.getImageUrl().equals("default")) {
            viewHolder.profile_image.setImageResource(R.mipmap.icon);
        } else {
            Glide.with(mContext).load(user.getImageUrl()).into(viewHolder.profile_image);
        }

        if (isChat) {
            lastMessages(user.getUser_id(), viewHolder.last_msg);
            unreadMsgEventListener(user.getUser_id(), viewHolder.badge);
        } else {
            viewHolder.last_msg.setVisibility(View.GONE);
            viewHolder.badge.setVisibility(View.GONE);
        }

        if (isChat) {
            if (user.getStatus().equals("online")) {
                viewHolder.img_on.setVisibility(View.VISIBLE);
                viewHolder.img_off.setVisibility(View.GONE);
            } else {
                viewHolder.img_on.setVisibility(View.GONE);
                viewHolder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.img_on.setVisibility(View.GONE);
            viewHolder.img_off.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getUser_id());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;
        private NotificationBadge badge;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            badge = itemView.findViewById(R.id.badge);
        }
    }

    private void unreadMsgEventListener(String userid, NotificationBadge badge) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                badge.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()
                                && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid)) {
                            unread++;


                            if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.isIsseen() ||
                                    chat.getReceiver().equals(userid)) {
                                unread = 0;
                            }

                        }

                    }


                }
                badge.setNumber(unread);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Check for last message
    private void lastMessages(final String userid, final TextView last_msg) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)
                                || chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                            mediaMessage = chat.getType();
                            doc_name = chat.getDocName();
                        }
                    }

                }
                switch (theLastMessage) {
                    case "default":
                        last_msg.setText(mContext.getString(R.string.no_message));
                        break;

                    default:
                        if (mediaMessage.equals("text")) {
                            last_msg.setText(theLastMessage);
                        } else if (mediaMessage.equals("\uD83D\uDCF7 Image")) {
                            last_msg.setText(mediaMessage);
                        } else if (mediaMessage.equals("\uD83D\uDCC1 Document")) {
                            last_msg.setText(doc_name);
                        } else if (mediaMessage.equals("\uD83C\uDF0D Location")) {
                            last_msg.setText(mediaMessage);

                        }

                        break;
                }
                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

