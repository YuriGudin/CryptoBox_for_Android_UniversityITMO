package com.cryptobox.cryptoboxforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.MultiAutoCompleteTextView;

import Util.UtilForAndroid;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        readInfoAboutProgram();
    }

    private void readInfoAboutProgram(){
        MultiAutoCompleteTextView TutorialMultiAutoCompleteTextView = findViewById(R.id.TutorialMultiAutoCompleteTextView);
        TutorialMultiAutoCompleteTextView.getText().clear();
        TutorialMultiAutoCompleteTextView.setText(UtilForAndroid.getStringFromRawFile(this, R.raw.tutorial));
    }
}
