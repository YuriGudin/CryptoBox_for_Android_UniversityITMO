package com.cryptobox.cryptoboxforandroid;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;


import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import CryptoCore.Hash;
import CryptoCore.RSA;
import Util.FileUtils;
import Util.UtilForAndroid;


public class AsymmetricEncryptionActivity extends AppCompatActivity {


    private static final String TAG = "PERMISSIONS";
    //Ключи
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String HashPrivateKeyFile; //Текущий хеш от файла закрытого ключа
    private String HashPublicKeyFile; //Текущий хеш от файла открытого ключа

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asymmetric_encryption);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button buttonRSA_SetPrivateKey = findViewById(R.id.buttonRSA_SetPrivateKey);
        buttonRSA_SetPrivateKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 1);
            }
        });

        Button buttonRSA_SetPublicKey = findViewById(R.id.buttonRSA_SetPublicKey);
        buttonRSA_SetPublicKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 2);
            }
        });

        Button buttonRSA_Encrypt = findViewById(R.id.buttonRSA_Encrypt);
        buttonRSA_Encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editTextRSA_out = findViewById(R.id.editTextRSA_out);
                try {
                    if (HashPublicKeyFile == null)
                        UtilForAndroid.Toast(AsymmetricEncryptionActivity.this, "Ошибка зашифрования: не выбран открытый ключ");
                    else {
                        if (editTextRSA_out.getText().toString().length() <= 227) {
                            editTextRSA_out.setText(RSA.encrypt(editTextRSA_out.getText().toString(), publicKey));
                            toClipBoard(editTextRSA_out.getText().toString(), "Шифртекст скопирован в буфер обмена");
                        } else
                            UtilForAndroid.Toast(AsymmetricEncryptionActivity.this, "Ошибка зашифрования, большой объем сообщения");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UtilForAndroid.Toast(AsymmetricEncryptionActivity.this, "Ошибка зашифрования");
                }
            }
        });

        Button buttonRSA_Decrypt = findViewById(R.id.buttonRSA_Decrypt);
        buttonRSA_Decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextRSA_out = findViewById(R.id.editTextRSA_out);
                try {
                    if (HashPrivateKeyFile == null)
                        UtilForAndroid.Toast(AsymmetricEncryptionActivity.this, "Ошибка расшифрования: не выбран закрытый ключ");
                    else {
                        editTextRSA_out.setText(RSA.decrypt(editTextRSA_out.getText().toString(), privateKey));
                        toClipBoard(editTextRSA_out.getText().toString(), "Расшифрованный текст скопирован в буфер обмена");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UtilForAndroid.Toast(AsymmetricEncryptionActivity.this, "Ошибка расшифрования");
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2: {
                if (resultCode == RESULT_OK) {
                    //динамическое получение прав на READ_EXTERNAL_STORAGE
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        android.util.Log.d(TAG, "Permission is granted");

                        File file = FileUtils.getFile(this, data.getData());
                        //UtilForAndroid.AlertDialogTest(AsymmetricEncryptionActivity.this, "Путь к файлу: " + file.getAbsolutePath()).show();
                        RSA rsa = new RSA();
                        try {
                            publicKey = rsa.LoadPublicKey(file.getAbsolutePath());
                            HashPublicKeyFile = Hash.comp(file, "SHA-512");
                            UtilForAndroid.AlertDialogTest(AsymmetricEncryptionActivity.this, "Открытый ключ установлен " + HashPublicKeyFile).show();
                            informationUpdate();
                            toClipBoard(HashPublicKeyFile, "Слепок открытого ключа скопирован в буфер обмена");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        }
                    } else {
                        android.util.Log.d(TAG, "Permission is revoked");
                        //запрашиваем разрешение
                        ActivityCompat.requestPermissions(AsymmetricEncryptionActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                }
                break;
            }
            case 1: {
                if (resultCode == RESULT_OK) {
                    //динамическое получение прав на READ_EXTERNAL_STORAGE
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        android.util.Log.d(TAG, "Permission is granted");

                        File file = FileUtils.getFile(this, data.getData());
                        //UtilForAndroid.AlertDialogTest(AsymmetricEncryptionActivity.this, "Путь к файлу: " + file.getAbsolutePath()).show();
                        RSA rsa = new RSA();
                        try {
                            HashPrivateKeyFile = Hash.comp(file, "SHA-512");
                            privateKey = rsa.LoadPrivateKey(file.getAbsolutePath());
                            UtilForAndroid.AlertDialogTest(AsymmetricEncryptionActivity.this, "Закрытый ключ установлен " + HashPrivateKeyFile).show();
                            informationUpdate();
                            toClipBoard(HashPrivateKeyFile, "Слепок закрытого ключа скопирован в буфер обмена");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        }

                    } else {
                        android.util.Log.d(TAG, "Permission is revoked");
                        //запрашиваем разрешение
                        ActivityCompat.requestPermissions(AsymmetricEncryptionActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }

                }
                break;
            }
        }
    }

    private void informationUpdate() {
        TextView textViewRSA_Information = findViewById(R.id.textViewRSA_Information);
        textViewRSA_Information.setText("");
        if (HashPublicKeyFile == null)
            textViewRSA_Information.append("SHA-512(public.key): НЕ УСТАНОВЛЕН\n");
        else
            textViewRSA_Information.append("SHA-512(public.key): " + HashPublicKeyFile + "\n");
        if (HashPrivateKeyFile == null)
            textViewRSA_Information.append("SHA-512(private.key): НЕ УСТАНОВЛЕН\n");
        else
            textViewRSA_Information.append("SHA-512(private.key): " + HashPrivateKeyFile + "\n");
    }

    private void toClipBoard(String text, String message){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("test", text);
        clipboard.setPrimaryClip(clip);
        UtilForAndroid.Toast(AsymmetricEncryptionActivity.this, message);
    }

}
