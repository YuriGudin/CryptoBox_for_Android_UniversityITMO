package com.cryptobox.cryptoboxforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.MultiAutoCompleteTextView;

import Util.UtilForAndroid;

public class ProgramVersionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_version);
        readInfoAboutProgram();
    }

    private void readInfoAboutProgram(){
        MultiAutoCompleteTextView VersionMultiAutoCompleteTextView = findViewById(R.id.VersionMultiAutoCompleteTextView);
        VersionMultiAutoCompleteTextView.getText().clear();
        VersionMultiAutoCompleteTextView.setText(UtilForAndroid.getStringFromRawFile(this, R.raw.about_program));
    }
}
