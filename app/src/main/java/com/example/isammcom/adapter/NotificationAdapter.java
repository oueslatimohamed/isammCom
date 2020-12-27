package com.example.isammcom.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.isammcom.R;
import com.example.isammcom.fragment.PostDetailFragment;
import com.example.isammcom.fragment.UserFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import model.Notification;
import model.Post;
import model.User;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent , false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification = mNotification.get(position);

        holder.text.setText(notification.getText());

        getUserInfo(holder.imageProfile , holder.username , notification.getUserid());

        if(notification.isIspost()){
            holder.postImage.setVisibility(View.VISIBLE);
            getPost(holder.imageProfile , notification.getPostid());
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notification.isIspost()){
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS" , Context.MODE_PRIVATE).edit();
                    editor.putString("postid" , notification.getPostid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PostDetailFragment()).commit();
                } else {

                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS" , Context.MODE_PRIVATE).edit();
                    editor.putString("profileid" , notification.getUserid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new UserFragment()).commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageProfile , postImage;
        public TextView username , text;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile_notif);
            postImage = itemView.findViewById(R.id.post_image_notif);
            username = itemView.findViewById(R.id.username_notif);
            text = itemView.findViewById(R.id.text_notif);
        }
    }

    private void getUserInfo(final ImageView imageView , final TextView username , String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImage()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPost(final ImageView imageView , String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostImage()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
