package com.cryptobox.cryptoboxforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rustamg.filedialogs.FileDialog;
import com.rustamg.filedialogs.OpenFileDialog;
import com.rustamg.filedialogs.SaveFileDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import CryptoCore.AES;
import Util.AndroidWindows;
import Util.PasswordGenerator;
import Util.SecurePasswordContainer;
import Util.UtilForAndroid;
import Util.WindowGetPasswordListener;

public class SymmetricEncryptionActivity extends AppCompatActivity  implements FileDialog.OnFileSelectedListener{


    private Button buttonSymmetricEncryptionSelectFile;
    private Button buttonRandomIV;
    private Button buttonSymmetricEncryptionEncrypt;
    private Button buttonSymmetricEncryptionDecrypt;
    private EditText editTextSymmetricEncryptionIV;
    private TextView textViewSymmetricEncryptionSelectFile;
    private FileDialog fileDialog;
    private File selectFile;
    private SecurePasswordContainer securePasswordContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symmetric_encryption);
        Init();

        buttonRandomIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextSymmetricEncryptionIV.setText(PasswordGenerator.generate(16, 16));
            }
        });

        buttonSymmetricEncryptionSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileDialog = new OpenFileDialog();
                fileDialog.show(getSupportFragmentManager(), "Открыть");
            }
        });

        buttonSymmetricEncryptionDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidWindows.WindowGetPassword(
                        SymmetricEncryptionActivity.this,
                                AndroidWindows.GET_PASSWORD_SETTING,
                                new WindowGetPasswordListener() {
                    @Override
                    public void onPositiveResult(SecurePasswordContainer SPC) {
                        fileDialog = new SaveFileDialog();
                        fileDialog.show(getSupportFragmentManager(), "Расшифровать");
                        securePasswordContainer = SPC;
                    }

                    @Override
                    public void onNegativeResult(String messageNegativeResult) {
                        UtilForAndroid.Toast(SymmetricEncryptionActivity.this, messageNegativeResult);
                    }
                }).show();
            }
        });

        buttonSymmetricEncryptionEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidWindows.WindowGetPassword(
                        SymmetricEncryptionActivity.this,
                                AndroidWindows.NEW_PASSWORD_SETTING,
                                new WindowGetPasswordListener() {
                    @Override
                    public void onPositiveResult(SecurePasswordContainer SPC) {
                        fileDialog = new SaveFileDialog();
                        fileDialog.show(getSupportFragmentManager(), "Зашифровать");
                        securePasswordContainer = SPC;
                    }

                    @Override
                    public void onNegativeResult(String messageNegativeResult) {
                        UtilForAndroid.Toast(SymmetricEncryptionActivity.this, messageNegativeResult);
                    }
                }).show();
            }
        });

    }

    private void Init(){
        buttonSymmetricEncryptionSelectFile = findViewById(R.id.buttonSymmetricEncryptionSelectFile);
        buttonRandomIV = findViewById(R.id.buttonRandomIV);
        buttonSymmetricEncryptionEncrypt = findViewById(R.id.buttonSymmetricEncryptionEncrypt);
        buttonSymmetricEncryptionDecrypt = findViewById(R.id.buttonSymmetricEncryptionDecrypt);
        editTextSymmetricEncryptionIV = findViewById(R.id.editTextSymmetricEncryptionIV);
        textViewSymmetricEncryptionSelectFile = findViewById(R.id.textViewSymmetricEncryptionSelectFile);
    }

    @Override
    public void onFileSelected(FileDialog dialog, File file) {

        String tag = dialog.getTag();

        if (tag.equals("Открыть")){
            selectFile = file;
            textViewSymmetricEncryptionSelectFile.setText("Выбранный файл: "+selectFile.getAbsolutePath());
        } else
            if (tag.equals("Расшифровать")){
            DecryptFileAES(file, securePasswordContainer);

        }
         else
            if (tag.equals("Зашифровать")){
                EncryptFileAES(file, securePasswordContainer);
            }
    }

    //Зашифровать один файл AES
    private void EncryptFileAES(File file, SecurePasswordContainer securePasswordContainer)
    {
        boolean flagOk = true; //Все ОК
        try {
            FileInputStream f = new FileInputStream(selectFile);
            byte[] fileInArray = new byte[(int)selectFile.length()];
            f.read(fileInArray);

            FileOutputStream fos=new FileOutputStream(file);
            try {
                fos.write(AES.encrypt(editTextSymmetricEncryptionIV.getText().toString(),
                        fileInArray,
                        securePasswordContainer.getPassword()));
            } catch (BadPaddingException e) {
                UtilForAndroid.Toast(SymmetricEncryptionActivity.this, "Ошибка зашифрования, проверьте правильность пароля. Участок: BadPaddingException");
                e.printStackTrace();
                flagOk=false;
            } catch (InvalidKeyException e) {
                UtilForAndroid.Toast(SymmetricEncryptionActivity.this, "Ошибка зашифрования, проверьте правильность пароля. Участок: InvalidKeyException");
                e.printStackTrace();
                flagOk=false;
            } catch (IllegalBlockSizeException e) {
                UtilForAndroid.Toast(SymmetricEncryptionActivity.this, "Ошибка зашифрования, проверьте правильность пароля. Участок: IllegalBlockSizeException");
                e.printStackTrace();
                flagOk=false;
            }
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            UtilForAndroid.Toast(SymmetricEncryptionActivity.this, "Ошибка зашифрования, не найден файл");
            e.printStackTrace();
            flagOk=false;
        } catch (IOException e) {
            UtilForAndroid.Toast(SymmetricEncryptionActivity.this, "Ошибка зашифрования, проблемы с вводом/выводом");
            e.printStackTrace();
            flagOk=false;
        }
        if (flagOk){
            UtilForAndroid.Toast(SymmetricEncryptionActivity.this, "Файл зашифрован");
        }
    }

    //Расшифровать один файл AES
    private void DecryptFileAES(File file, SecurePasswordContainer securePasswordContainer)
    {
        boolean flagOk = true; //Все ОК
        try {
            FileInputStream f  = new FileInputStream(selectFile);
            byte[] fileInArray = new byte[(int)selectFile.length()];
            f.read(fileInArray);

            FileOutputStream fos=new FileOutputStream(file);
            try {
                fos.write(AES.decrypt(editTextSymmetricEncryptionIV.getText().toString(),
                        fileInArray,
                        securePasswordContainer.getPassword()));
            } catch (BadPaddingException e) {
                UtilForAndroid.Toast(SymmetricEncryptionActivity.this, "Ошибка расшифрования, проверьте правильность пароля. Участок: BadPaddingException");
                e.printStackTrace();
                flagOk=false;
            } catch (InvalidKeyException e) {
                UtilForAndroid.Toast(SymmetricEncryptionActivity.this, "Ошибка расшифрования, проверьте правильность пароля. Участок: InvalidKeyException");
                e.printStackTrace();
                flagOk=false;
            } catch (IllegalBlockSizeException e) {
                UtilForAndroid.Toast(SymmetricEncryptionActivity.this, "Ошибка расшифрования, проверьте правильность пароля. Участок: IllegalBlockSizeException");
                e.printStackTrace();
                flagOk=false;
            }
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            UtilForAndroid.Toast(SymmetricEncryptionActivity.this, "Ошибка расшифрования, не найден файл");
            e.printStackTrace();
            flagOk=false;
        } catch (IOException e) {
            UtilForAndroid.Toast(SymmetricEncryptionActivity.this, "Ошибка расшифрования, проблемы с вводом/выводом");
            e.printStackTrace();
            flagOk=false;
        }
        if (flagOk){
            UtilForAndroid.Toast(SymmetricEncryptionActivity.this, "Файл расшифрован");
        }
    }
}