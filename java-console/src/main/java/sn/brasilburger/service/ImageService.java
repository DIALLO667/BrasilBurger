package sn.brasilburger.service;

import java.io.File;

public interface ImageService {
    
    String uploadImage(File imageFile);
    
    String uploadImage(File imageFile, int width, int height);
    
    boolean deleteImage(String imageUrl);
    
    boolean validateImageFormat(File imageFile);
}