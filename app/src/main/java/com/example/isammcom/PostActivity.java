package com.example.isammcom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    Uri mImageUri;
    String mUrl = "";
    StorageTask mUploadTask;
    StorageReference mStorageReference;

    ImageView mClose, mPost_added;
    TextView mPost;
    EditText mDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mClose = (ImageView) findViewById(R.id.close);
        mPost_added = (ImageView) findViewById(R.id.post_added);
        mPost = (TextView) findViewById(R.id.post_txt);
        mDescription = (EditText) findViewById(R.id.descripition_text);

        mStorageReference = FirebaseStorage.getInstance().getReference("Posts");

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this , MainActivity.class));
                finish();
            }
        });

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        CropImage.activity().setAspectRatio(1,1).start(PostActivity.this);

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Chargement en cours ....");
        progressDialog.show();

        if(mImageUri != null){
            final StorageReference storageReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            mUploadTask = storageReference.putFile(mImageUri);
            mUploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        mUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postid = reference.push().getKey();

                        HashMap<String , Object> hashMap = new HashMap<>();
                        hashMap.put("postId" , postid);
                        hashMap.put("postImage" , mUrl);
                        hashMap.put("description" , mDescription.getText().toString());
                        hashMap.put("publisher" , FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postid).setValue(hashMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(PostActivity.this , MainActivity.class));
                        finish();

                    } else {
                        Toast.makeText(PostActivity.this , "Erreur .. " , Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this , ""+e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this , "Aucune image selectionnée ..." , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            mPost_added.setImageURI(mImageUri);
        } else {
            Toast.makeText(this , "réessayer une autre fois" , Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this , MainActivity.class));
            finish();
        }

    }
}