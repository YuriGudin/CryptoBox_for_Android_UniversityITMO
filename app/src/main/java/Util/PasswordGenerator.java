package Util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

public final class PasswordGenerator {

    public static String generate(int from, int to) {
        String pass  = "";
        SecureRandom secureRandom = new SecureRandom();
        int cntchars = from + secureRandom.nextInt(to - from + 1);
        for (int i = 0; i < cntchars; ++i) {
            char next = 0;
            int range = 10;
            switch(secureRandom.nextInt(3)) {
                case 0: {next = '0'; range = 10;} break;
                case 1: {next = 'a'; range = 26;} break;
                case 2: {next = 'A'; range = 26;} break;
            }
            pass += (char)((secureRandom.nextInt(range)) + next);
        }

        return pass;
    }



    /**
     * Метод, генерирующий пароль
     *
     * @param AZ Включить набор символов ABCDEFGHIJKLMNOPQRSTUVWXYZ
     * @param az Включить набор символов abcdefghijklmnopqrstuvwxyz
     * @param num Включить набор символов 0123456789
     * @param spec Включить набор символов /\#*+-_.:,;!\"§$%&(){[]}?@~|<>
     * @param length Длина пароля
     * @return Сгенерированный пароль
     */
    public static String generate(boolean AZ, boolean az, boolean num, boolean spec, int length ){
        int len=length;
        ArrayList<Character> buf = new ArrayList<Character>();

        if (AZ){
            String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            for (int i = 0; i <s.length() ; i++) {
                buf.add(s.charAt(i));
            }
        }

        if (az){
            String s = "abcdefghijklmnopqrstuvwxyz";
            for (int i = 0; i <s.length() ; i++) {
                buf.add(s.charAt(i));
            }

        }

        if (num){
            String s = "0123456789";
            for (int i = 0; i <s.length() ; i++) {
                buf.add(s.charAt(i));
            }
        }

        if (spec){
            String s = "/\\\\#*+-_.:,;!\\\"§$%&(){[]}?@~|<>";
            for (int i = 0; i <s.length() ; i++) {
                buf.add(s.charAt(i));
            }
        }

        Random random = null;
        try
        {
            random = SecureRandom.getInstance("SHA1PRNG");
        }
        catch (NoSuchAlgorithmException ex)
        {
            ex.printStackTrace();
        }

        StringBuilder strBuilder = new StringBuilder();

        while(len-- > 0)
            strBuilder.append(buf.get(random.nextInt(buf.size())));

        return  strBuilder.toString();
    }


}