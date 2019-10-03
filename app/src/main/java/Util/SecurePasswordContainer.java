package Util;


import CryptoCore.AES;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.util.Arrays;

public class SecurePasswordContainer {

    private byte[] password;
    private String iv;
    private String key;


    public void setPassword(String password) {
        try {
            this.password = AES.encrypt(this.key=PasswordGenerator.generate(true, true, true, true, 256),
                    password.getBytes(), this.iv=PasswordGenerator.generate(true, true, true, true, 256));
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public String getPassword() {
        try {
            return new String(AES.decrypt(key, password, iv));
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        SecurePasswordContainer securePasswordContainer = new SecurePasswordContainer();
        securePasswordContainer.setPassword("qwerty12345");
        System.out.println(securePasswordContainer.iv);
        System.out.println(securePasswordContainer.key);
        System.out.println(Arrays.toString(securePasswordContainer.password));
        System.out.println();
        System.out.println(securePasswordContainer.getPassword());
    }
}
