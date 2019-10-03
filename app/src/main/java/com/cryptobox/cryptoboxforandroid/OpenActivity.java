package com.cryptobox.cryptoboxforandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.security.InvalidKeyException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import DataBase.Connector;
import Util.AndroidWindows;
import Util.FileUtils;
import Util.SecurePasswordContainer;
import Util.UtilForAndroid;
import Util.WindowGetPasswordListener;
import tableMapping.FileSignature;


public class OpenActivity extends Activity {

    static class Item {
       private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        private String price;

        Item(String name, String price) {
            this.name = name;
            this.price = price;
        }
    }

    private class ItemsAdapter extends ArrayAdapter<Item> {
        ItemsAdapter() {
            super(OpenActivity.this, R.layout.item);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final View view = getLayoutInflater().inflate(R.layout.item, null);
            final Item item = getItem(position);
            ((TextView) view.findViewById(R.id.name)).setText(item.name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtilForAndroid.Toast(OpenActivity.this, item.getName()+" "+item.getPrice());
                }
            });

            return view;
        }
    }



    private EditText editTextOpenSearch;
    private Connector connectorCryptoBoxContainer=null;
    private Button buttonOpenBoxSearch;
    private Button buttonOpenBoxAddFile;
    private ListView listViewOpenBoxItems;
    private ItemsAdapter adapter;

    //Инициализатор
    private void Init(){
        editTextOpenSearch = findViewById(R.id.editTextOpenSearch);
        buttonOpenBoxSearch = findViewById(R.id.buttonOpenBoxSearch);
        buttonOpenBoxAddFile = findViewById(R.id.buttonOpenBoxAddFile);
        listViewOpenBoxItems = findViewById(R.id.listViewOpenBoxItems);
        adapter = new ItemsAdapter();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        Init();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 1);

        listViewOpenBoxItems.setAdapter(adapter);
        buttonOpenBoxAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.add(new Item("Тест", "Тест"));
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectorCryptoBoxContainer!=null)
            connectorCryptoBoxContainer.CloseDB();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    final File fileDataBase = FileUtils.getFile(OpenActivity.this, data.getData());
                    //editTextOpenBox_out.append("База данных: "+fileDataBase.getAbsolutePath()+"\n");
                    UtilForAndroid.Toast(OpenActivity.this, "База данных: "+fileDataBase.getAbsolutePath());
                    AndroidWindows.WindowGetPassword(OpenActivity.this, AndroidWindows.GET_PASSWORD_SETTING, new WindowGetPasswordListener() {
                        @Override
                        public void onPositiveResult(SecurePasswordContainer securePasswordContainer) {
                            UtilForAndroid.Toast(OpenActivity.this, securePasswordContainer.getPassword());
                            readDataBase(securePasswordContainer, fileDataBase);
                        }

                        @Override
                        public void onNegativeResult(String messageNegativeResult) {
                            UtilForAndroid.Toast(OpenActivity.this, messageNegativeResult);
                        }
                    }).show();

                }
                break;
            }
        }
    }

    private void readDataBase(SecurePasswordContainer securePasswordContainerForDataBase, File fileDataBase){
        connectorCryptoBoxContainer = new Connector();
       // editTextOpenBox_out.append("Содержимое: "+"\n");
        connectorCryptoBoxContainer.setSecurePasswordContainer(securePasswordContainerForDataBase);
        try {
            connectorCryptoBoxContainer.Conn(fileDataBase.getAbsolutePath());
            ArrayList<FileSignature> fileSignatureArrayList = connectorCryptoBoxContainer.readFileSignature();
            for (FileSignature fs:fileSignatureArrayList) {
                //editTextOpenBox_out.append(fs.getId()+" "+fs.getFileName()+"\n");
                adapter.add(new Item(fs.getFileName(), String.valueOf(fs.getId())));
            }
           // editTextOpenBox_out.setVisibility(View.VISIBLE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }



}
