package sn.brasilburger.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static Connection connection = null;
    
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Properties props = new Properties();
                InputStream input = DatabaseConfig.class
                    .getClassLoader()
                    .getResourceAsStream("database.properties");
                
                if (input == null) {
                    throw new Exception("database.properties introuvable");
                }
                
                props.load(input);
                
                String url = props.getProperty("db.url");
                String username = props.getProperty("db.username");
                String password = props.getProperty("db.password");
                String driver = props.getProperty("db.driver");
                
                Class.forName(driver);
                connection = DriverManager.getConnection(url, username, password);
                
                System.out.println("Connexion BD reussie !");
                
            } catch (Exception e) {
                System.err.println("Erreur BD : " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}