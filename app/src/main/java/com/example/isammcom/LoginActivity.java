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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
     TextView mSignLink;
     EditText mEmail;
     EditText mPassword;
     Button mConnectBtn;

     ProgressDialog pd;
     FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = (EditText) findViewById(R.id.activity_login_email);
        mPassword = (EditText) findViewById(R.id.activity_login_password);
        mConnectBtn = (Button) findViewById(R.id.activity_login_connectbtn);

        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Veuillez patienter ...");
                pd.show();

                String str_email = mEmail.getText().toString();
                String str_password = mPassword.getText().toString();

                if( TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){

                    Toast.makeText(LoginActivity.this,"Remplir tout les champs !", Toast.LENGTH_SHORT).show();
                    pd.dismiss();

                } else {
                    auth.signInWithEmailAndPassword(str_email , str_password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Students")
                                                .child(auth.getCurrentUser().getUid());
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(auth.getCurrentUser().isEmailVerified()){
                                                    pd.dismiss();
                                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                } else {
                                                    pd.dismiss();
                                                    Toast.makeText(LoginActivity.this,"Verifiez votre adresse mail ..", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                pd.dismiss();
                                            }
                                        });
                                    } else {
                                        pd.dismiss();
                                        Toast.makeText(LoginActivity.this,"Ce compte n'existe pas...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        //Passage LoginActivity ---->SignupActivity a partir textview (S'inscrire)
        mSignLink = (TextView) findViewById(R.id.inscriretv);
        mSignLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
    }
}