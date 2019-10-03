package com.cryptobox.cryptoboxforandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.rustamg.filedialogs.FileDialog;
import com.rustamg.filedialogs.SaveFileDialog;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import CryptoCore.Hash;
import DataBase.Connector;
import Util.AndroidWindows;
import Util.FileUtils;
import Util.SecurePasswordContainer;
import Util.UtilForAndroid;
import Util.WindowGetPasswordListener;


public class MainActivity extends AppCompatActivity implements FileDialog.OnFileSelectedListener {

    private static final String TAG = "PERMISSIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermissions(); //Получение необходимых разрешений для приложения


        Button buttonTest = findViewById(R.id.buttonTest);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Это тестовая кнопка, не тыкай!", Toast.LENGTH_SHORT).show();
                AndroidWindows.WindowGetPassword(MainActivity.this, AndroidWindows.NEW_PASSWORD_SETTING, new WindowGetPasswordListener() {
                    @Override
                    public void onPositiveResult(SecurePasswordContainer securePasswordContainer) {
                        UtilForAndroid.Toast(MainActivity.this, securePasswordContainer.getPassword());
                    }

                    @Override
                    public void onNegativeResult(String messageNegativeResult) {
                        UtilForAndroid.Toast(MainActivity.this, messageNegativeResult);
                    }
                }).show();
            }
        });

        Button buttonOpen = findViewById(R.id.buttonOpen);
        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OpenActivity.class);
                startActivity(intent);
            }
        });

        Button buttonRabinCryptoSystem = findViewById(R.id.buttonRabinCryptoSystem);
        buttonRabinCryptoSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RabinCryptoSystemActivity.class);
                startActivity(intent);
            }
        });

        Button buttonCheckSum = findViewById(R.id.buttonCheckSum);
        buttonCheckSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(MainActivity.this, CheckSumActivity.class);
                startActivity(intent);*/
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 1);
            }
        });

        Button buttonAssymetricEncryption = findViewById(R.id.buttonAssymetricEncryption);
        buttonAssymetricEncryption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AsymmetricEncryptionActivity.class);
                startActivity(intent);
            }
        });

        Button buttonGenerator = findViewById(R.id.buttonGenerator);
        buttonGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PasswordGeneratorActivity.class);
                startActivity(intent);

            }
        });

        Button buttonAboutProgram = findViewById(R.id.buttonAboutProgram);
        buttonAboutProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutProgramActivity.class);
                startActivity(intent);
            }
        });

        Button buttonCreate = findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveFileDialog saveFileDialog  = new SaveFileDialog();
                saveFileDialog.show(getSupportFragmentManager(), "Создание контейнера");
                UtilForAndroid.Toast(MainActivity.this, saveFileDialog.getTag());
            }
        });

        Button buttonGeneratorRSAKeys = findViewById(R.id.buttonGeneratorRSAKeys);
        buttonGeneratorRSAKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Generate_RSA_KeysActivity.class);
                startActivity(intent);
            }
        });

        Button buttonSymmetricEncryption = findViewById(R.id.buttonSymmetricEncryption);
        buttonSymmetricEncryption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SymmetricEncryptionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    //динамическое получение прав на READ_EXTERNAL_STORAGE
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        android.util.Log.d(TAG, "Permission is granted");
                        File fileCheckSum = FileUtils.getFile(this, data.getData());
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Контрольные суммы для файла " + fileCheckSum.getAbsolutePath()+":\n\n");
                        try {
                            stringBuilder.append("SHA-512: " + Hash.comp(fileCheckSum, "SHA-512")+"\n\n");
                            stringBuilder.append("SHA-256: " + Hash.comp(fileCheckSum, "SHA-256")+"\n\n");
                            stringBuilder.append("SHA-1: " + Hash.comp(fileCheckSum, "SHA-1")+"\n\n");
                            stringBuilder.append("MD5: " + Hash.comp(fileCheckSum, "MD5")+"\n\n");
                            AlertDialog alertDialog = UtilForAndroid.AlertDialogTest(this, stringBuilder.toString());
                            alertDialog.setTitle("Контрольное суммирование");
                            alertDialog.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            UtilForAndroid.Toast(this, "Ошибка ввода/вывода");
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                            UtilForAndroid.Toast(this, "Не найден алгоритм для выполнения контрольного суммирования");
                        }
                    } else {
                        android.util.Log.d(TAG, "Permission is revoked");
                        //запрашиваем разрешение
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onFileSelected(FileDialog dialog, File file) {

        if (dialog.getTag().equals("Создание контейнера")){
            try {
                Connector.CreateDataBase(file.getAbsolutePath());
                UtilForAndroid.Toast(MainActivity.this, "Контейнер "+file.getName()+" cоздан");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}