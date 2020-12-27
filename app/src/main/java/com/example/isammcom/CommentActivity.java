package com.example.isammcom;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.isammcom.R;
import com.example.isammcom.adapter.CommentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Comment;
import model.User;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    EditText mAddComment;
    ImageView mImageProfile;
    ImageView mSendBtn;
    Toolbar mToolbar;


    String postid;
    String publisherid;

    FirebaseUser firebaseUser;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mToolbar = findViewById(R.id.toolbar_comment);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view_comment);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this , commentList);
        recyclerView.setAdapter(commentAdapter);

        mAddComment = findViewById(R.id.add_comment);
        mImageProfile = findViewById(R.id.image_profile_comment);
        mSendBtn = findViewById(R.id.sendBtn);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAddComment.getText().toString().equals("")){
                    Toast.makeText(CommentActivity.this , "Vous ne pouvez pas envoyer un commentaire vide" , Toast.LENGTH_SHORT).show();
                } else {
                    addcomment();
                }
            }
        });

        getImage();
        readComment();
    }

    private void addcomment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("comment" , mAddComment.getText().toString());
        hashMap.put("publisher" , firebaseUser.getUid());

        reference.push().setValue(hashMap);
        addNotifications();
        mAddComment.setText("");

    }

    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("userid" , firebaseUser.getUid());
        hashMap.put("text" , "a commenté : "+ mAddComment.getText().toString());
        hashMap.put("postid" , postid);
        hashMap.put("ispost" , true);

        reference.push().setValue(hashMap);
    }

    private void getImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImage()).into(mImageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Comment comment = snapshot1.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}