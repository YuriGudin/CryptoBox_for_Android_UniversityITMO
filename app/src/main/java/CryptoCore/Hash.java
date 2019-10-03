package CryptoCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Hash {

    private static final int BUF_SIZE = 8192;

    //Перевод в шестнадцатеричный формат
    private static String toHex(byte[] bytes){
        StringBuilder builder = new StringBuilder();
        for (byte b:bytes)
            builder.append(String.format("%02X",b));
        return builder.toString();
    }

    /**
     * Вычисление контрольной суммы MD2, MD5, SHA-1, SHA-224, SHA-256, SHA-512, SHA-384
     * @param file Файл, для которого будет вычислина контрольная сумма
     * @param algorithm Алгоритм контрольного суммирования
     * @return Контрольная сумма в шестнадцатеричной системе счисления
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String comp (File file, String algorithm) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        try(FileInputStream fin=new FileInputStream(file))
        {
            byte[] b = new byte[BUF_SIZE];
            int r;
            while((r=fin.read(b)) >= 0){
                if (r < BUF_SIZE)  md.update(Arrays.copyOf(b, r));
                else  md.update(b);
            }
        }
        return toHex(md.digest());
    }


    /**
     * Вычисление контрольной суммы MD2, MD5, SHA-1, SHA-224, SHA-256, SHA-512, SHA-384
     * @param text Строка, для которой будет вычислена контрольная сумма
     * @param algorithm Алгоритм контрольного суммирования
     * @return Контрольная сумма в шестнадцатеричной системе счисления
     * @throws NoSuchAlgorithmException
     */
    public static String comp (String text, String algorithm) throws NoSuchAlgorithmException {
        return toHex(MessageDigest.getInstance(algorithm).digest(text.getBytes()));
    }

    /**
     * Вычисление контрольной суммы MD2, MD5, SHA-1, SHA-224, SHA-256, SHA-512, SHA-384
     * @param bytes Массив байт, для которого будет вычислена контрольная мумм
     * @param algorithm Алгоритм контрольного суммирования
     * @return Контрольная сумма в шестнадцатеричной системе счисления
     * @throws NoSuchAlgorithmException
     */
    public static String comp(byte [] bytes, String algorithm) throws NoSuchAlgorithmException {
        return toHex(MessageDigest.getInstance(algorithm).digest(bytes));
    }

    public static void main(String[] args) {

    }
}
