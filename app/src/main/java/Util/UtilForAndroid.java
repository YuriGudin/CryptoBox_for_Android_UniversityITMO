package Util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.cryptobox.cryptoboxforandroid.MainActivity;
import com.cryptobox.cryptoboxforandroid.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class UtilForAndroid {

    private static AlertDialog.Builder builder;

    public static AlertDialog AlertDialogTest (@NonNull Context context){
        return Builder(context).create();
    }

    public static AlertDialog AlertDialogTest (@NonNull Context context, String message){
        return Builder(context).setMessage(message).create();
    }

    private static AlertDialog.Builder Builder(@NonNull Context context){
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Сообщение")
                .setCancelable(false)
                .setPositiveButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        return builder;
    }

    public static String getStringFromRawFile(Activity activity, int id_res) {
        Resources r = activity.getResources();
        InputStream is = r.openRawResource(id_res);
        String myText = null;
        try {
            myText = convertStreamToString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  myText;
    }

    private static String  convertStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = is.read();
        while( i != -1)
        {
            baos.write(i);
            i = is.read();
        }
        return  baos.toString();
    }

    public static void Toast(Activity activity, String text){
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

}
