package sn.brasilburger.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.InputStream;
import java.util.Properties;

public class CloudinaryConfig {
    private static Cloudinary cloudinary = null;
    
    public static Cloudinary getInstance() {
        if (cloudinary == null) {
            try {
                Properties props = new Properties();
                InputStream input = CloudinaryConfig.class
                    .getClassLoader()
                    .getResourceAsStream("cloudinary.properties");
                
                if (input == null) {
                    throw new Exception("cloudinary.properties introuvable");
                }
                
                props.load(input);
                
                cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", props.getProperty("cloudinary.cloud_name"),
                    "api_key", props.getProperty("cloudinary.api_key"),
                    "api_secret", props.getProperty("cloudinary.api_secret"),
                    "secure", true
                ));
                
                System.out.println("Cloudinary configure");
                
            } catch (Exception e) {
                System.err.println("Erreur Cloudinary : " + e.getMessage());
            }
        }
        return cloudinary;
    }
}