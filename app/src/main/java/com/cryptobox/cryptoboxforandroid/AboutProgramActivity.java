package com.cryptobox.cryptoboxforandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AboutProgramActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_program);

        Button buttonProgramVersion = findViewById(R.id.buttonProgramVersion);

        buttonProgramVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutProgramActivity.this, ProgramVersionActivity.class);
                startActivity(intent);
            }
        });

        Button buttonTutorial = findViewById(R.id.buttonTutorial);
        buttonTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutProgramActivity.this, TutorialActivity.class);
                startActivity(intent);
            }
        });

        Button buttonInformation = findViewById(R.id.buttonInformation);
        buttonInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutProgramActivity.this, TestInfoActivity.class);
                startActivity(intent);
            }
        });
    }



}
