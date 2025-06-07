package application;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnect {
    private static final String url = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12783511";
    private static final String name = "sql12783511";
    private static final String password = "que55X2Yg4";

    public DBConnect() {
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, name, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
