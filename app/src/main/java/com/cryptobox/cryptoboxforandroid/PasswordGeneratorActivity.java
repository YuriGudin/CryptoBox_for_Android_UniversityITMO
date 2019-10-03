package com.cryptobox.cryptoboxforandroid;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import Util.PasswordGenerator;
import Util.UtilForAndroid;

public class PasswordGeneratorActivity extends AppCompatActivity {

    private EditText editTextPasswordGenerator_out;
    private EditText editTextPasswordCount;
    private EditText editTextPasswordLength;
    private Button buttonGeneratePassword;

    private CheckBox checkbox_AZ;
    private CheckBox checkbox_az;
    private CheckBox checkbox_09;
    private CheckBox checkbox_spec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_generator);

        Init();

        buttonGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int length = Integer.valueOf(editTextPasswordLength.getText().toString());
                int count = Integer.valueOf(editTextPasswordCount.getText().toString());

                if (checkbox_AZ.isChecked() || checkbox_az.isChecked() || checkbox_09.isChecked() || checkbox_spec.isChecked()) {

                    editTextPasswordGenerator_out.setText("");
                    for (int i = 0; i < count; i++) {
                        editTextPasswordGenerator_out.append(PasswordGenerator.generate(
                                checkbox_AZ.isChecked(),
                                checkbox_az.isChecked(),
                                checkbox_09.isChecked(),
                                checkbox_spec.isChecked(), length) + "\n");
                    }
                }
                else {
                    UtilForAndroid.Toast(PasswordGeneratorActivity.this, "Должен быть выбран по меньшей мере один набор знаков.");
                }
            }

        });
    }

    private void Init(){
        editTextPasswordGenerator_out=findViewById(R.id.editTextPasswordGenerator_out);
        editTextPasswordCount = findViewById(R.id.editTextPasswordCount);
        editTextPasswordLength = findViewById(R.id.editTextPasswordLength);
        buttonGeneratePassword = findViewById(R.id.buttonGeneratePassword);

        checkbox_az = findViewById(R.id.checkbox_az);
        checkbox_AZ = findViewById(R.id.checkbox_AZ);
        checkbox_09 = findViewById(R.id.checkbox_09);
        checkbox_spec = findViewById(R.id.checkbox_spec);

        checkbox_az.setChecked(true);
        checkbox_AZ.setChecked(true);
        checkbox_09.setChecked(true);
    }
}
