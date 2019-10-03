package com.cryptobox.cryptoboxforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigInteger;
import java.security.SecureRandom;


import CryptoCore.RabinCryptoSystem;
import Util.UtilForAndroid;

public class RabinCryptoSystemActivity extends AppCompatActivity {


    private EditText editTextP;
    private EditText editTextQ;
    private EditText editTextN;
    private EditText editTextOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rabin_crypto_system);

        Init();

        Button buttonClear = findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextN.setText("");
                editTextP.setText("");
                editTextQ.setText("");
                editTextOut.setText("");
            }
        });

        Button buttonComputeN = findViewById(R.id.buttonComputeN);

        buttonComputeN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BigInteger p = new BigInteger(editTextP.getText().toString());
                BigInteger q = new BigInteger(editTextQ.getText().toString());
                BigInteger n = p.multiply(q);
                editTextN.setText(n.toString());
                toClipBoard(n.toString(), "Открытый ключ n скопирован в буфер обмена");
            }
        });

        Button buttonComputeEncrypt = findViewById(R.id.buttonEncrypt);

        buttonComputeEncrypt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String message = editTextOut.getText().toString();
                BigInteger n = new BigInteger(editTextN.getText().toString());
                System.out.println(n.toString());
                byte [] c = RabinCryptoSystem.rabinEncipherWSalt(message.getBytes(), n, new SecureRandom());
                editTextOut.setText(Base64.encodeToString(c, Base64.DEFAULT));
                toClipBoard(editTextOut.getText().toString(), "Шифртекст скопирован в буфер обмена");
            }
        });

        Button buttonDecrypt = findViewById(R.id.buttonDecrypt);

        buttonDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String CipherText = editTextOut.getText().toString();
                BigInteger p = new BigInteger(editTextP.getText().toString());
                BigInteger q = new BigInteger(editTextQ.getText().toString());
                byte [] c = RabinCryptoSystem.rabinDecipherWSalt(Base64.decode(CipherText, Base64.DEFAULT), p, q);
                String OpenText = new String(c);
                editTextOut.setText(OpenText);
                toClipBoard(OpenText, "Расшифрованный текст скопирован в буфер обмена");
            }
        });

        Button buttonRabinCSGenPQ = findViewById(R.id.buttonRabinCSGenPQ);
        buttonRabinCSGenPQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BigInteger p = RabinCryptoSystem.getPrime();
                BigInteger q = RabinCryptoSystem.getPrime();
                editTextQ.setText(p.toString());
                editTextP.setText(q.toString());
                toClipBoard("p = "+p.toString()+"\nq = "+q.toString(), "Параметры p и q (Закрытй ключ) скопированы в буфер обмена");
            }
        });
    }

    private void toClipBoard(String text, String message){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("test", text);
        clipboard.setPrimaryClip(clip);
        UtilForAndroid.Toast(RabinCryptoSystemActivity.this, message);
    }
    private void Init() {
         editTextP = findViewById(R.id.editTextP);
         editTextQ = findViewById(R.id.editTextQ);
         editTextN = findViewById(R.id.editTextN);
         editTextOut = findViewById(R.id.editTextOut);

        editTextP.setText("1160860262941237786501460749339");
        editTextQ.setText("1155626756844787902598035530767");
        editTextOut.setText("Test");
    }
}
