package sn.brasilburger.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import sn.brasilburger.config.CloudinaryConfig;
import sn.brasilburger.service.ImageService;

import java.io.File;
import java.util.Map;

public class CloudinaryImageService implements ImageService {
    
    private Cloudinary cloudinary;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final String[] ALLOWED_FORMATS = {"jpg", "jpeg", "png", "webp"};
    
    public CloudinaryImageService() {
        this.cloudinary = CloudinaryConfig.getInstance();
    }
    
    @Override
    public String uploadImage(File imageFile) {
        return uploadImage(imageFile, 800, 800);
    }
    
    @Override
    public String uploadImage(File imageFile, int width, int height) {
        // Validation 1 : Fichier existe
        if (imageFile == null || !imageFile.exists()) {
            System.err.println("❌ Fichier image introuvable");
            return null;
        }
        
        // Validation 2 : Taille fichier
        if (imageFile.length() > MAX_FILE_SIZE) {
            System.err.println("❌ Fichier trop volumineux (max 5MB)");
            return null;
        }
        
        // Validation 3 : Format
        if (!validateImageFormat(imageFile)) {
            System.err.println("❌ Format image non supporté (jpg, jpeg, png, webp uniquement)");
            return null;
        }
        
        try {
            System.out.println("⏳ Upload de l'image en cours...");
            
            // Configuration upload avec transformation
            Map uploadResult = cloudinary.uploader().upload(imageFile, 
                ObjectUtils.asMap(
                    "folder", "brasil-burger",
                    "transformation", new Transformation()
                        .width(width)
                        .height(height)
                        .crop("fill")
                        .quality("auto"),
                    "use_filename", true,
                    "unique_filename", true
                )
            );
            
            String imageUrl = (String) uploadResult.get("secure_url");
            System.out.println("✅ Image uploadée avec succès !");
            System.out.println("   URL : " + imageUrl);
            
            return imageUrl;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur upload image : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            System.err.println("⚠️  URL image vide, rien à supprimer");
            return false;
        }
        
        try {
            // Extraire le public_id depuis l'URL
            String publicId = extractPublicIdFromUrl(imageUrl);
            
            if (publicId == null) {
                System.err.println("❌ Impossible d'extraire le public_id de l'URL");
                return false;
            }
            
            System.out.println("⏳ Suppression de l'image...");
            
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String resultStatus = (String) result.get("result");
            
            if ("ok".equals(resultStatus)) {
                System.out.println("✅ Image supprimée avec succès");
                return true;
            } else {
                System.err.println("⚠️  Suppression image : " + resultStatus);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur suppression image : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean validateImageFormat(File imageFile) {
        String fileName = imageFile.getName().toLowerCase();
        
        for (String format : ALLOWED_FORMATS) {
            if (fileName.endsWith("." + format)) {
                return true;
            }
        }
        
        return false;
    }
    
 
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            // Trouver "brasil-burger/" dans l'URL
            int folderIndex = imageUrl.indexOf("brasil-burger/");
            if (folderIndex == -1) {
                return null;
            }
            
            // Extraire depuis "brasil-burger/" jusqu'à la fin
            String pathWithExtension = imageUrl.substring(folderIndex);
            
            // Enlever l'extension (.jpg, .png, etc.)
            int lastDotIndex = pathWithExtension.lastIndexOf(".");
            if (lastDotIndex != -1) {
                return pathWithExtension.substring(0, lastDotIndex);
            }
            
            return pathWithExtension;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur extraction public_id : " + e.getMessage());
            return null;
        }
    }
}