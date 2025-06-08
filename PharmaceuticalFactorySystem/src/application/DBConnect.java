package application;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnect {
    private static final String url = "jdbc:mysql://62.84.184.87:3306/pharmaceuticalfactory";
    private static final String name = "admin";
    private static final String password = "admin";

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
