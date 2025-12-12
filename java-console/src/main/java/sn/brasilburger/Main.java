package sn.brasilburger;

import sn.brasilburger.config.DatabaseConfig;
import sn.brasilburger.config.CloudinaryConfig;
import java.sql.Connection;
import com.cloudinary.Cloudinary;

public class Main {
    public static void main(String[] args) {
        System.out.println("Brasil Burger - Gestion Catalogue");
        System.out.println("==================================\n");
        
        Connection conn = DatabaseConfig.getConnection();
        if (conn != null) {
            System.out.println("BD OK !\n");
        }
        
        Cloudinary cloudinary = CloudinaryConfig.getInstance();
        if (cloudinary != null) {
            System.out.println("Cloudinary OK !\n");
        }
        
        System.out.println("Tous les services OK !");
        DatabaseConfig.closeConnection();
    }
}