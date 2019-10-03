package com.cryptobox.cryptoboxforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.rustamg.filedialogs.FileDialog;
import com.rustamg.filedialogs.SaveFileDialog;

import java.io.File;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import CryptoCore.RSA;
import Util.UtilForAndroid;


public class Generate_RSA_KeysActivity extends AppCompatActivity implements FileDialog.OnFileSelectedListener{

    private EditText editTextGenRSA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate__rsa__keys);

        Init();
        Button buttonRsa_Generate_Keys = findViewById(R.id.buttonRsa_Generate_Keys);
        buttonRsa_Generate_Keys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveFileDialog saveDialog = new SaveFileDialog();
                saveDialog.show(getSupportFragmentManager(), "Тест");
            }
        });
    }

    @Override
    public void onFileSelected(FileDialog dialog, File file) {
        UtilForAndroid.Toast(Generate_RSA_KeysActivity.this, file.getAbsolutePath());
        String path = file.getAbsolutePath();
        /*path = path.substring(0, path.lastIndexOf("/"));*/

        if (!file.exists()) {
            file.mkdir();
        }
        UtilForAndroid.Toast(Generate_RSA_KeysActivity.this, path);
        genRSA_Keys(path);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Генерирует RSA ключи
    private void genRSA_Keys(String path){
        RSA rsa = new RSA();
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(Integer.parseInt(editTextGenRSA.getText().toString())); //Считать размер из формы
            KeyPair kp = kpg.genKeyPair();

            rsa.SaveKeyPair(path, kp);
            UtilForAndroid.Toast(Generate_RSA_KeysActivity.this, "Ключи успешно сгенерированы.\n"+
                    "Путь сохранения ключей: "+path+"/*.key");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void Init(){
        editTextGenRSA = findViewById(R.id.editTextGenRSA);
    }
}
