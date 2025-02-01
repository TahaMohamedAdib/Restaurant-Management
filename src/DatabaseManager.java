import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static final String CONFIG_FILE = "config.properties";
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        // Charger la configuration depuis le fichier
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");

            // Charger le pilote MySQL une seule fois
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading configuration or MySQL driver: " + e.getMessage());
            throw new RuntimeException("Failed to initialize DatabaseManager", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try {
            Connection connection = getConnection();
            System.out.println("Connection to database was successful!");
            connection.close();
        } catch (SQLException e) {
            System.err.println("Error: Unable to connect to database.");
            e.printStackTrace();
        }
    }
}