package CryptoCore;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class AES {


    public static byte[] encrypt(String key, byte[] data, String iv) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        return doFinal(key, data, iv, Cipher.ENCRYPT_MODE);
    }

    public static byte[] decrypt (String key, byte[]data, String iv) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        return doFinal(key, data, iv, Cipher.DECRYPT_MODE);
    }

    /**
     * Метод выполняет криптографические преобразования AES/CBC/PKCS5Padding в соответствии с выбранным режимом: зашифрования или расшифрования
     *
     * @param key Ключ для преобразований
     * @param data Данные для преобразований
     * @param iv Вектор инициализации
     * @param mode Режимы шифрования в соответствии с режимами {@link Cipher}
     * @return Преобразованные данные
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private static byte[] doFinal(String key, byte[]data, String iv, final int mode) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] keyBytes=hashPlainTextToByte16(key); //Преобразование байтов для выравнивания
        byte[] ivBytes=hashPlainTextToByte16(iv); //Преобразование байтов для выравнивания
        Cipher cipher = null;
        try {
           cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
           SecretKeySpec sks=new SecretKeySpec(keyBytes, "AES");
           cipher.init(mode, sks, new IvParameterSpec(ivBytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        assert cipher != null;
        return cipher.doFinal(data);
    }

    /**
     * Метод преобразует plainText с помощью криптографической хеш-функции и берет первые 16 байт SHA-512
     * @param plainText password  или iv
     *
     * @return 16 байт обрезанного SHA-512
     */
    private static byte[] hashPlainTextToByte16(String plainText){
        MessageDigest md = null;
        try {
            md= MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte tempkey []=md.digest(plainText.getBytes());
        byte[] Bytes=new byte[16];
        for (int i = 0; i <16; i++)
            Bytes[i]=tempkey[i];

        return Bytes;
    }

    private static  String  BytesToString(byte[] bytes) {
        StringBuilder SB = new StringBuilder();
        for (int i=0; i<bytes.length; i++)
            SB.append(bytes[i]+" ");
        return SB.toString();
    }
}