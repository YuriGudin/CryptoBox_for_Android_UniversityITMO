package DataBase;

import CryptoCore.AES;
import CryptoCore.Base64;
import CryptoCore.Hash;


import Util.SecurePasswordContainer;
import Util.Utility;
import tableMapping.FileSignature;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;



public class Connector {
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public void setSecurePasswordContainer(SecurePasswordContainer securePasswordContainer) {
        this.securePasswordContainer = securePasswordContainer;
    }

    private SecurePasswordContainer securePasswordContainer;

    public SecurePasswordContainer getSecurePasswordContainer() {
        return securePasswordContainer;
    }

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public void Conn(String PathToDataBase) throws ClassNotFoundException, SQLException
    {

        connection = null;
        Class.forName("org.h2.Driver");
        Utility.print_info(PathToDataBase);

        if (PathToDataBase.endsWith(".h2.db")){
            PathToDataBase = PathToDataBase.substring(0, PathToDataBase.length()-6);
        }
        System.out.println("DriverManager.getConnection("+"jdbc:h2:"+PathToDataBase+")");
        connection = DriverManager.getConnection("jdbc:h2:"+PathToDataBase);

        Utility.print_good("Подключение к базе данных выполнено");
    }

    //Удаление файла из базы данных по индексу файла
    public void DelFileDB(long index) throws SQLException {
        String  query = "DELETE FROM files WHERE id="+index;
        statement = connection.createStatement();
        statement.execute(query);

    }

    public byte[] GetFileDB(long index) throws SQLException {
        String  query = "SELECT File FROM files WHERE id ="+index;
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);
        Utility.print_SQL_Query(query);
        resultSet.next();
        return decrypt(resultSet.getBytes("File"));
    }


    // --------Создание базы данных--------
    public static void CreateDataBase(String PathContainerCryptoBox) throws SQLException, ClassNotFoundException {
       Connector connector = new Connector();
       connector.Conn(PathContainerCryptoBox);
       connector.CreateTableDB();
       connector.CloseDB();
    }

    // --------Создание таблицы--------
    public void CreateTableDB() throws SQLException
    {
        statement = connection.createStatement();
        statement.execute("DROP TABLE IF EXISTS files");
        statement.execute("CREATE TABLE files (id INTEGER PRIMARY KEY AUTO_INCREMENT, FileName text, File BLOB);");
    }

    // --------Заполнение таблицы--------
    public void AddFileDB(File file) throws SQLException
    {
        String  query = "INSERT INTO files (FileName, File) VALUES(?,?)";
        PreparedStatement pst = connection.prepareStatement(query);

        pst.setString(1, encrypt(file.getName()));
        pst.setBytes(2, encrypt(readFile(file)));
        pst.executeUpdate();

        Utility.print_SQL_Query(query);
        pst.close();
    }

    // --------Получение сигнатур файлов (без самих файлов)--------
    public ArrayList<FileSignature> readFileSignature() throws SQLException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        ArrayList<FileSignature> fileSignatureArrayList = new ArrayList<>();
        statement = connection.createStatement();
        String query = "SELECT id, FileName FROM files";
        resultSet = statement.executeQuery(query);

        while (resultSet.next()){
            long id = resultSet.getLong("id");
            String fileName = decrypt(resultSet.getString("FileName"));
            fileSignatureArrayList.add(new FileSignature(id, fileName));
        }

        return fileSignatureArrayList;
    }

    public byte[] readFileBytes(long index) {
        try {
            String  query = "SELECT File FROM files WHERE id ="+index;
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            return  decrypt(resultSet.getBytes("File"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // -------- Вывод таблицы--------
    public void ReadDB_test_NoDecrypt() throws SQLException
    {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM files");
        FileOutputStream fos = null;

        int i=0;
        while(resultSet.next())
        {
            i++;
            System.out.println("i = "+i);
            int id = resultSet.getInt("id");
            String  name = resultSet.getString("FileName");
            File file = new File("test_out/tempFile_"+i);
            try {
                fos = new FileOutputStream(file);
                InputStream input = resultSet.getBinaryStream("File");
                byte[] buffer = new byte[1024];
                while (input.read(buffer) > 0) {
                    fos.write(buffer);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println( "ID = " + id );
            System.out.println(name);
            System.out.println("NameFile = "+file.getAbsolutePath());

            System.out.println();
        }

    }

    // --------Закрытие--------
    public void CloseDB()
    {
        try {
            connection.close();
            statement.close();
            if (resultSet!=null)
                resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Utility.print_good("Соединения закрыты");
    }

    /**
     * Читает файл и возвращает байтовый массив
     * @param file
     * @return Байтовый массив
     */
    private byte[] readFile(File file) {
        ByteArrayOutputStream bos = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1;) {
                bos.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e2) {
            System.err.println(e2.getMessage());
        }
        return bos != null ? bos.toByteArray() : null;
    }


    /**
     * Метод зашифрования информации с помощью {@link SecurePasswordContainer}
     * @param string Строка для зашифрования
     * @return Зашифрованная информация, закодированная
     */
    private String encrypt(String string)  {
        try {
            return Base64.encodeBytes(
                              AES.encrypt(securePasswordContainer.getPassword(),
                                      string.getBytes(),
                                      Hash.comp(securePasswordContainer.getPassword(), "SHA-512")));

        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Метод расшифрования информации с помощью {@link SecurePasswordContainer}
     * @param string Строка для расшифрования, закодированная {@link Base64}
     * @return Расшифрованная информация
     */
    private String decrypt(String string) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        try {
            return  new String(
                    AES.decrypt(securePasswordContainer.getPassword(),
                            (Base64.decode(string)),
                            Hash.comp(securePasswordContainer.getPassword(), "SHA-512")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * Метод зашифрования информации с помощью {@link SecurePasswordContainer}
     * @param data Данные для зашифрования
     * @return Зашифрованные данные или {@link null} если возникли ошибки
     */
    private byte[] encrypt(byte [] data){
        try {
            return AES.encrypt(securePasswordContainer.getPassword(), data, Hash.comp(securePasswordContainer.getPassword(), "SHA-512"));
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод расшифрования информации с помощью {@link SecurePasswordContainer}
     * @param data Данные для расшифрования
     * @return Расшифрованные данные или {@link null} если возникли ошибки
     */
    private byte[] decrypt(byte [] data){
        try {
            return AES.decrypt(securePasswordContainer.getPassword(), data, Hash.comp(securePasswordContainer.getPassword(), "SHA-512"));
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}