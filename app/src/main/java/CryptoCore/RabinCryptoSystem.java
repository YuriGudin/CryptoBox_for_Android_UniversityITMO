package CryptoCore;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.NoSuchElementException;

public class RabinCryptoSystem {

    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private static final BigInteger FOUR = BigInteger.valueOf(4);

    //Метод преобразующий число в последовательность битов с помощью метода toByteArray()
    private static byte[] getBytes(BigInteger big) {
        byte[] bigBytes = big.toByteArray();

        if (big.bitLength() % 8 != 0) return bigBytes;
        else {
            byte[] smallerBytes = new byte[big.bitLength() / 8];
            System.arraycopy(bigBytes,1,smallerBytes,0,smallerBytes.length);
            return smallerBytes;
        }
    }

    //Метод преобразующий одномерный массив данных в блоки данных
    private static byte[][] block(byte[] msg,int blockSize) {
        //Создание двухмерного массива байтов соответствующего сообщения - все блоки должны быть заполнены
        int numberOfBlocks = msg.length / blockSize;
        byte[][] ba = new byte[numberOfBlocks][blockSize];

        for (int i = 0; i < numberOfBlocks; i++)
            for (int j = 0; j < blockSize; j++)
                ba[i][j] = msg[i * blockSize + j];
        return ba;
    }

    //Метод преобразующий блоки данных в одномерный массив данных
    private static byte[] unBlock(byte[][] ba, int blockSize) {
        //Создание одномерного массива, в котором будут расположены дешифрованные блоки
        byte[] m2 = new byte[ba.length * blockSize];

        //Помещение блоков в одномерный массив
        for (int i = 0; i < ba.length; i++) {
            int j = blockSize - 1;
            int k = ba[i].length - 1;
            while (k >= 0) {
                m2[i * blockSize + j] = ba[i][k];
                k--;
                j--;
            }
        }
        return m2;
    }

    //Метод выполняющий добивку сообщения до полного блока данных
    private static byte[] pad(byte[] msg,int blockSize) {
        //Убеждаемся, что размер блока соответствует добивке PKCS#5
        if (blockSize < 1 || blockSize > 255) throw new
                IllegalArgumentException("Размер блока должен быть между 1 и 255.");
        //Добивка сообщения до полного блока
        int numberToPad = blockSize - msg.length % blockSize;
        byte[] paddedMsg = new byte[msg.length + numberToPad];
        System.arraycopy(msg,0, paddedMsg,0, msg.length);

        for (int i = msg.length; i < paddedMsg.length; i++)
            paddedMsg[i] = (byte)numberToPad;
        return paddedMsg;
    }

    //Метод убирающий добивку из сообщения
    private static byte[] unPad(byte[] msg, int blockSize) {
        //Опеределение объема добивки - просто смотрим на последний блок
        int numberOfPads = (msg[msg.length - 1] + 256) % 256;
        //Вычитаем длину добивки и возвращаем массив
        byte[] answer = new byte[msg.length - numberOfPads];
        System.arraycopy(msg, 0, answer, 0, answer.length);
        return answer;
    }

    //Метод добавляющий избыточность и соль к сообщению, используемый в методе шифрования по Рабину
    private static byte[] addRedundancyAndSalt(byte[] b, SecureRandom random) {
        byte[] answer = new byte[b.length+8];
        byte[] salt = new byte[4];

        random.nextBytes(salt);

        //Склеиваем подсоленное и избыточное сообщение
        System.arraycopy(salt,0,answer,0,4);          //Вставка соли в начало сообщения
        System.arraycopy(b,0,answer,4,4);             //Далее, вставка избыточности (первые 4 байта сообщения)
        System.arraycopy(b,0,answer,8,b.length);            //На конец, дополняем строку самим сообщением

        return answer;
    }

    //Метод удаляющий избыточность и соль из сообщения, используемый в методе дешифрования по Рабину
    private static byte[] removeRedundancyAndSalt(byte[] b) {
        byte[] answer = new byte[b.length-8];

        System.arraycopy(b,8,answer,0,answer.length);        //Копируем сообщение целиком

        return answer;
    }

    //Метод вычисляющий наименьший неотрицательный остаток (least nonnegative residue "lnr") от b mod m, где m > 0.
    private static BigInteger lnr(BigInteger b, BigInteger m) {

        if (m.compareTo(BigInteger.ZERO) <= 0)
            throw new IllegalArgumentException("Модуль должен быть положительным.");

        BigInteger answer = b.mod(m);

        return (answer.compareTo(BigInteger.ZERO) < 0) ? answer.add(m) : answer;
    }

    //Метод открытого шифрования по Рабину (сообщение с солью)
    public static byte[] rabinEncipherWSalt(byte[] msg, BigInteger n, SecureRandom sr)
    {
        //Вычисление размера блока открытого текста - убираем 4 байта соли и 4 байта избыточности сообщения
        int blockSize = (n.bitLength() - 1) / 8;

        if (blockSize < 12) throw new IllegalArgumentException
                ("Длина модуля должна быть >= 12 байтам");
        byte[][] ba = block(pad(msg,blockSize - 8),blockSize - 8);
        //Выполняем шифровывание
        for (int i = 0; i < ba.length; i++) {
            ba[i] = addRedundancyAndSalt(ba[i], sr);
            ba[i] = getBytes(new BigInteger(1, ba[i]).modPow(TWO, n));
        }

        //Возвращаем одномерный массив. Размер блока зашифрованного текста на один байт больше размера блока открытого текста
        return unBlock(ba,blockSize+1);
    }

    //Метод дешифровки для открытого шифрования по Рабину (сообщение с солью)
    public static byte[] rabinDecipherWSalt(byte[] msg, BigInteger p, BigInteger q) throws IllegalArgumentException {
        //Вычисляем обратные значения для p mod q, и для q mod p
        BigInteger n = p.multiply(q);
        BigInteger pinv = p.modInverse(q);
        BigInteger qinv = q.modInverse(p);
        BigInteger pexp = (p.add(ONE)).divide(FOUR);
        BigInteger qexp = (q.add(ONE)).divide(FOUR);

        //Вычисления размера блока шифртекста
        int blockSize = ((n.bitLength() - 1) / 8 )+ 1;
        byte[][] ba = block(msg,blockSize);

        //Выполняем дешифровку
        for (int i = 0; i < ba.length; i++) {
            //Вычисляем 4 корня по китайской теореме об остатках
            BigInteger term1 = new BigInteger(1,ba[i]).modPow(pexp,n).multiply(q).multiply(qinv);
            BigInteger term2 = new BigInteger(1,ba[i]).modPow(qexp,n).multiply(p).multiply(pinv);
            byte[][] msgroot = new byte[4][0];
            BigInteger sum = term1.add(term2);
            BigInteger difference = term1.subtract(term2);
            msgroot[0] = getBytes(lnr(sum,n));
            msgroot[1] = getBytes(lnr(sum.negate(),n));
            msgroot[2] = getBytes(lnr(difference,n));
            msgroot[3] = getBytes(lnr(difference.negate(),n));

            boolean[] isCorrectRoot = new boolean[4];

            //Ищем правильный корень (с помощью избыточности исходного сообщения)
            for (int k = 0; k < 4; k++) {
                isCorrectRoot[k] = true;
                for (int j = 4; j < 8; j++)
                    if (msgroot[k][j] != msgroot[k][j+4]) {
                        isCorrectRoot[k] = false;
                        break;
                    }
            }
            boolean correctFound = false;
            for (int k = 0; k < 4; k++) if (isCorrectRoot[k]) {
                if (!correctFound) {
                    correctFound = true;
                    ba[i]=msgroot[k];
                }
                else {
                    ba[i]=null;
                    throw new IllegalArgumentException
                            ("Несколько сообщений удовлетворяют требованию избыточности!");
                }
            }
            if (!correctFound) throw new NoSuchElementException
                    ("Ни одно сообщение не удовлетворяет требованию избыточности!");
            ba[i] = removeRedundancyAndSalt(ba[i]);
        }
        //Переход от блоков к одномерному масиву и уаление добивки. Вернуть полученный результат
        return unPad(unBlock(ba,blockSize-9),blockSize-9);
    }

    public static BigInteger getPrime(){
        BigInteger p;
        SecureRandom secureRandom = new SecureRandom();
        while (true) {
            p = new BigInteger(12*8, 100, secureRandom);
            if (p.mod(FOUR).equals(THREE))
                break;
        }
        return p;
    }
    public static void main(String[] args) {

        SecureRandom sr = new SecureRandom();

        BigInteger p = new BigInteger("1160860262941237786501460749339");
        BigInteger q = new BigInteger("1155626756844787902598035530767");
        BigInteger n = p.multiply(q);

        String mesage = "Меседж трололо";
        byte [] c = rabinEncipherWSalt(mesage.getBytes(), n, sr);
        String C = new String(c);
        System.out.println(C);

        String D = new String(rabinDecipherWSalt(c, p, q));
        System.out.println(D);
    }
}
