package com.cryptobox.cryptoboxforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.MultiAutoCompleteTextView;

import Util.UtilForAndroid;

public class TestInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_info);
        readInfoAboutProgram();
    }

    private void readInfoAboutProgram(){
        MultiAutoCompleteTextView TestInfoAutoCompleteTextView = findViewById(R.id.TestInfoAutoCompleteTextView);
        TestInfoAutoCompleteTextView.getText().clear();
        TestInfoAutoCompleteTextView.setText(UtilForAndroid.getStringFromRawFile(this, R.raw.test_info));
    }
}
