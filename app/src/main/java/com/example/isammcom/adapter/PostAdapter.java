package com.example.isammcom.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.isammcom.CommentActivity;
import com.example.isammcom.R;
import com.example.isammcom.fragment.PostDetailFragment;
import com.example.isammcom.fragment.UserFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import model.Post;
import model.User;

import static android.content.ContentValues.TAG;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item , parent , false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(position);

        Glide.with(mContext).load(post.getPostImage()).into(holder.post_image);

        if(post.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        publisherInfo(holder.image_profile,holder.username,holder.publisher,post.getPublisher());
        isLikes(post.getPostId() , holder.like);
        nrLikes(holder.likes , post.getPostId());
        getComment(post.getPostId(), holder.comments);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.like.getTag().equals("J'adore")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotifications(post.getPublisher() , post.getPostId());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid" , post.getPostId());
                intent.putExtra("publisherid" , post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid" , post.getPostId());
                intent.putExtra("publisherid" , post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS" , Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment()).commit();
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS" , Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment()).commit();
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS" , Context.MODE_PRIVATE).edit();
                editor.putString("postid",post.getPostId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile;
        public ImageView post_image;
        public ImageView like;
        public ImageView comment;

        public TextView username;
        public TextView likes;
        public TextView publisher;
        public TextView description;
        public TextView comments;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile_home);
            post_image = itemView.findViewById(R.id.post_home);
            like = itemView.findViewById(R.id.like_home);
            comment = itemView.findViewById(R.id.com_home);
            username = itemView.findViewById(R.id.username_home);
            likes = itemView.findViewById(R.id.likes_home);
            publisher = itemView.findViewById(R.id.publisher_home);
            description = itemView.findViewById(R.id.descripition_home);
            comments = itemView.findViewById(R.id.comments_home);

        }
    }

    private  void getComment(String postid , final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText("Voir plus "+snapshot.getChildrenCount() + " Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLikes(String postId , final ImageView postImage) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    postImage.setImageResource(R.drawable.ic_liked);
                    postImage.setTag("Adoré");
                } else {
                    postImage.setImageResource(R.drawable.ic_favorite);
                    postImage.setTag("J'adore");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotifications(String userid , String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("userid" , firebaseUser.getUid());
        hashMap.put("text" , "Adore votre post");
        hashMap.put("postid" , postid);
        hashMap.put("ispost" , true);

        reference.push().setValue(hashMap);
    }

    private void nrLikes(final TextView likes , String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + " Adores");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void publisherInfo(final ImageView image_profile , final TextView username , final TextView publisher , final String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                    Glide.with(mContext).load(user.getImage()).into(image_profile);
                    username.setText(user.getUsername());
                    publisher.setText(user.getUsername());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
