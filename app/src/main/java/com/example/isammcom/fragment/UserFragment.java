package com.example.isammcom.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.isammcom.EditProfileActivity;
import com.example.isammcom.R;
import com.example.isammcom.adapter.MyPhotoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import model.Post;
import model.User;


public class UserFragment extends Fragment {

    ImageView imageProfile;
    TextView posts , followrs,following,fullname,bio,username;
    Button editProfile;

    RecyclerView recyclerView;
    MyPhotoAdapter myPhotoAdapter;
    List<Post> postList;

    FirebaseUser firebaseUser;
    String profileid;

    ImageButton myPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREPS" , Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid" , "none");

        imageProfile = view.findViewById(R.id.image_profile_profile);
        posts = view.findViewById(R.id.posts_profile);
        followrs = view.findViewById(R.id.followrs_profile);
        following = view.findViewById(R.id.following_profile);
        fullname = view.findViewById(R.id.fullname_profile);
        bio = view.findViewById(R.id.bio_profile);
        username = view.findViewById(R.id.username_profile);
        editProfile = view.findViewById(R.id.edit_profile_btn);
        myPhoto = view.findViewById(R.id.my_fotos);

        recyclerView = view.findViewById(R.id.recyclerview_image_profile);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myPhotoAdapter = new MyPhotoAdapter(getContext(),postList);
        recyclerView.setAdapter(myPhotoAdapter);

        userInfo();
        getFollowers();
        getNrPost();
        myPhotos();

        if(profileid.equals(firebaseUser.getUid())){
            editProfile.setText("Modifier");
        } else {
            checkFollow();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = editProfile.getText().toString();

                if(btn.equals("Modifier")){
                  startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else if(btn.equals("S'abonner")){
                    FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid())
                            .child("Following").child(profileid).setValue(false);
                    FirebaseDatabase.getInstance().getReference("Follow").child(profileid)
                            .child("Followers").child(firebaseUser.getUid()).setValue(true);

                    addNotifications();
                } else if (btn.equals("Abonnée")){
                    FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid())
                            .child("Following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference("Follow").child(profileid)
                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        return view;
    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getContext() == null){
                    return;
                }

                User user = snapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImage()).into(imageProfile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("userid" , firebaseUser.getUid());
        hashMap.put("text" , "a commencé à vous suivre ");
        hashMap.put("postid" , "");
        hashMap.put("ispost" , false);

        reference.push().setValue(hashMap);
    }

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileid).exists()){
                    editProfile.setText("Abonnée");
                } else {
                    editProfile.setText("S'abonner");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("Followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followrs.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("Following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getNrPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int i = 0;
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        Post post = snapshot1.getValue(Post.class);
                        if(post.getPublisher().equals(profileid)){
                            i++;
                        }
                    }

                    posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Post post = snapshot1.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}