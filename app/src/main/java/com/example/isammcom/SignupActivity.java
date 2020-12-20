 package com.example.isammcom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    TextView mConnect;
    EditText mName;
    EditText mUsername;
    EditText mEmail;
    EditText mPassword;
    Button mRegister;
    ProgressDialog pd;
   FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mName = (EditText) findViewById(R.id.activity_signup_name);
        mUsername = (EditText) findViewById(R.id.activity_signup_username);
        mEmail = (EditText) findViewById(R.id.activity_signup_email);
        mPassword = (EditText) findViewById(R.id.activity_signup_password);
        mRegister = (Button) findViewById(R.id.activity_signup_btnSign);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               pd = new ProgressDialog(SignupActivity.this);
               pd.setMessage("Veuillez patienter ....");
               pd.show();

               String str_name = mName.getText().toString();
               String str_username = mUsername.getText().toString();
               String str_email = mEmail.getText().toString();
               String str_password = mPassword.getText().toString();

               if(TextUtils.isEmpty(str_name) || TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                   pd.dismiss();
                   Toast.makeText(SignupActivity.this,"Remplir tout les champs !", Toast.LENGTH_SHORT).show();

               } else {

                        register(str_name,str_username,str_email,str_password);
               }


            }
        });



        //Passage SignupActivitty ----> LoginActivity a partir textview (se connecter)
        mConnect = (TextView) findViewById(R.id.activity_signup_connectTV);
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
        });
    }


    private void register (final String name , final String username , final String email , final String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        auth.getCurrentUser().sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            final FirebaseUser firebaseUser = auth.getCurrentUser();
                                            String studid = firebaseUser.getUid();

                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Students").child(studid);
                                                HashMap<String , Object> hashMap = new HashMap<>();
                                                hashMap.put("id" , studid);
                                                hashMap.put("fullname" , name);
                                                hashMap.put("username" , username);
                                                hashMap.put("email" , email);
                                                hashMap.put("password" , password);
                                                hashMap.put("bio" , "");
                                                hashMap.put("image" , "https://firebasestorage.googleapis.com/v0/b/isammcom-a978c.appspot.com/o/avatar.png?alt=media&token=fb4b5693-eae3-48cb-8953-c5debd2305cc");

                                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            pd.dismiss();
                                                            Intent intent = new Intent(SignupActivity.this , LoginActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);

                                                        }
                                                    }
                                                });

                                        } else {
                                            pd.dismiss();
                                            Toast.makeText(SignupActivity.this , "email is already exist ",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
    }

}