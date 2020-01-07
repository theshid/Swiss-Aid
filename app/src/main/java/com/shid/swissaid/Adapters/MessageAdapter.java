package com.shid.swissaid.Adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shid.swissaid.Model.Chat;
import com.shid.swissaid.R;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> mChat;
    private String imageUrl;

    FirebaseUser firebaseUser;
    private onRecyclerViewItemClickListener mItemClickListener;

    private String fExtension = ".jpeg";
    private String fDestination = "/storage/emulated/0/SwissAid/Image/";
    private String fName;


    public void file_download(String url, String fileName) {
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "SwissAid");
        File subFolder = new File(Environment.getExternalStorageDirectory().toString(), "SwissAid/Image/Sent");
        File sFolder = new File(Environment.getExternalStorageDirectory().toString(), "SwissAid/Image");
        if (!folder.exists()) {
            folder.mkdir();
            if (!sFolder.exists()){
                sFolder.mkdirs();
                if (!subFolder.exists()) {
                    subFolder.mkdirs();
                }
            }

        } else if (folder.exists()){
            if (!sFolder.exists()) {
                sFolder.mkdirs();
                if (!subFolder.exists()) {
                    subFolder.mkdirs();
                }
            }
        }


        DownloadManager mgr = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Demo")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir("/SwissAid/Image", fileName);

        mgr.enqueue(request);

    }


    public void setOnItemClickListener(MessageAdapter.onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, int position);
    }

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageUrl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int position) {
        Chat chat = mChat.get(position);
        String imagePath = chat.getSentImagePath();
        String message_type = chat.getType();
        String imageMsgUrl = chat.getMessage();
        if (message_type.equals("text")) {
            viewHolder.show_message.setText(chat.getMessage());
            viewHolder.show_time.setText(chat.getTime());
            viewHolder.show_document.setVisibility(View.GONE);
            viewHolder.image_message.setVisibility(View.GONE);
        } else if (message_type.equals("\uD83D\uDCF7 Image")) {
            viewHolder.show_message.setVisibility(View.GONE);
            viewHolder.image_message.setVisibility(View.VISIBLE);
            viewHolder.show_document.setVisibility(View.GONE);
            viewHolder.show_time.setText(chat.getTime());
            String filename = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            File imgFile = new File(Environment.getExternalStorageDirectory()
                    + "/SwissAid/Image/Sent/" + filename );
            File imgDl = new File(Environment.getExternalStorageDirectory()
                    + "/SwissAid/Image" + filename );
            if (imgFile.exists()) {
                viewHolder.image_message.setImageURI(Uri.parse(String.valueOf(imgFile)));
            } else {
                //downloadFile(mContext, filename, fExtension, fDestination, imageUrl);
                file_download(imageMsgUrl,filename);
                if (imgDl.exists()) {
                    viewHolder.image_message.setImageURI(Uri.parse(String.valueOf(imgDl)));
                } else{
                    Glide.with(mContext).load(chat.getMessage()).into(viewHolder.image_message);
                }
            }

        } else if (message_type.equals("\uD83D\uDCC1 Document")) {
            viewHolder.show_message.setVisibility(View.GONE);
            viewHolder.image_message.setVisibility(View.GONE);
            viewHolder.show_document.setVisibility(View.VISIBLE);
            viewHolder.show_document.setText(chat.getDocName());
            viewHolder.show_time.setText(chat.getTime());
        } else if (message_type.equals("\uD83C\uDF0D Location")) {
            viewHolder.show_message.setVisibility(View.GONE);
            viewHolder.image_message.setVisibility(View.VISIBLE);
            viewHolder.show_document.setVisibility(View.GONE);
            viewHolder.show_time.setText(chat.getTime());
            Glide.with(mContext).load(chat.getMessage()).into(viewHolder.image_message);
        }
        /*
        viewHolder.show_message.setText(chat.getMessage());
        viewHolder.show_time.setText(chat.getTime());
        */

        if (imageUrl.equals("default")) {
            viewHolder.profile_image.setImageResource(R.mipmap.icon);
        } else {
            Glide.with(mContext).load(imageUrl).into(viewHolder.profile_image);
        }

        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                viewHolder.txt_seen.setText(mContext.getString(R.string.seen));
            } else {
                viewHolder.txt_seen.setText(mContext.getString(R.string.delivered));
            }
        } else {
            viewHolder.txt_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView show_message;
        public TextView show_document;
        public ImageView profile_image;
        public TextView txt_seen;
        public TextView show_time;
        public ImageView image_message;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            show_document = itemView.findViewById(R.id.show_document);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            show_time = itemView.findViewById(R.id.show_time);
            image_message = itemView.findViewById(R.id.image_message);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}


