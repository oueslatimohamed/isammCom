package com.example.isammcom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    Button connectez ;
    Button inscrire ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        connectez = findViewById(R.id.connectez);
        inscrire = findViewById(R.id.inscrire);

        //Passage StartActivity ----> Activity_login
        connectez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startActivity(new Intent(StartActivity.this,LoginActivity.class));
            }
        });

        inscrire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this,SignupActivity.class));
            }
        });
    }
}