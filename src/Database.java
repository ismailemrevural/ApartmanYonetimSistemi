import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/apartmanyonetimsistemi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Her seferinde yeni bir bağlantı yaratılacak, static kullanılmaz.
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Bağlantı başarılı!");
            return connection;
        } catch (SQLException e) {
            System.err.println("MySQL bağlantısı sırasında bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
