package com.example.isammcom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    Button mConnect ;
    Button mSign ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mConnect = (Button) findViewById(R.id.connectez);
        mSign = (Button) findViewById(R.id.inscrire);

        //Passage StartActivity ----> Activity_login
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startActivity(new Intent(StartActivity.this,LoginActivity.class));
            }
        });

        /*mSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this,SignupActivity.class));
            }
        });*/
    }
}