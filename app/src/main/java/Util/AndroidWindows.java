package Util;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.cryptobox.cryptoboxforandroid.R;

class NullSecurePasswordException extends Exception {

    NullSecurePasswordException(){}
    public NullSecurePasswordException(String message){
        super(message);
    }

}

public class AndroidWindows {



    public static final int GET_PASSWORD_SETTING = 0;
    public static final int NEW_PASSWORD_SETTING = 1;


    //Окно взятия пароля
    public static AlertDialog WindowGetPassword(final Activity activity, final int password_setting, final WindowGetPasswordListener windowGetPasswordListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final LayoutInflater inflater = activity.getLayoutInflater();
        View view = null;
         if (password_setting==NEW_PASSWORD_SETTING) {
            view= inflater.inflate(R.layout.password_dialog_new, null);
            builder.setTitle("Придумайте пароль");
         }
         else {
             view=inflater.inflate(R.layout.password_dialog_get, null);
             builder.setTitle("Введите пароль");
         }
        builder.setView(view);

        final View finalView = view;
        builder.setPositiveButton(R.string.continue_, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                   windowGetPasswordListener.onPositiveResult(getSecurePasswordContainer());
                } catch (NullSecurePasswordException e) {
                    //e.printStackTrace();
                }
            }

            private SecurePasswordContainer getSecurePasswordContainer() throws NullSecurePasswordException {
                SecurePasswordContainer securePasswordContainer = new SecurePasswordContainer();
                if (password_setting==NEW_PASSWORD_SETTING) {
                    EditText PasswordDialogPassword = finalView.findViewById(R.id.PasswordDialogPassword);
                    EditText PasswordDialogConfirmPassword = finalView.findViewById(R.id.PasswordDialogConfirmPassword);
                    if (PasswordDialogConfirmPassword.getText().toString().equals(
                            PasswordDialogPassword.getText().toString())){
                        securePasswordContainer.setPassword(PasswordDialogPassword.getText().toString());
                        return securePasswordContainer;
                    }
                    else {
                        windowGetPasswordListener.onNegativeResult("Пароли не совпадают, повторите ввод");
                        PasswordDialogPassword.getText().clear();
                        PasswordDialogConfirmPassword.getText().clear();
                        throw new NullSecurePasswordException();
                    }
                } else if (password_setting==GET_PASSWORD_SETTING) {
                    EditText PasswordDialogPasswordNew = finalView.findViewById(R.id.PasswordDialogPasswordNew);
                    securePasswordContainer.setPassword(PasswordDialogPasswordNew.getText().toString());
                    return securePasswordContainer;
                }
                return securePasswordContainer;
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                UtilForAndroid.Toast(activity, "Действие отменено");
                dialog.cancel();
            }
        });

        return builder.create();
    }
}
