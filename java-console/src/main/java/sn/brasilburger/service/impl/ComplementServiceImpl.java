package sn.brasilburger.service.impl;

import sn.brasilburger.entity.Complement;
import sn.brasilburger.entity.enums.TypeComplement;
import sn.brasilburger.repository.ComplementRepository;
import sn.brasilburger.repository.impl.ComplementRepositoryImpl;
import sn.brasilburger.service.ComplementService;
import sn.brasilburger.service.ImageService;

import java.io.File;
import java.util.List;

public class ComplementServiceImpl implements ComplementService {
    
    private ComplementRepository complementRepository;
    private ImageService imageService;
    
    public ComplementServiceImpl() {
        this.complementRepository = new ComplementRepositoryImpl();
        this.imageService = new CloudinaryImageService();
    }
    
    public ComplementServiceImpl(ComplementRepository complementRepository, ImageService imageService) {
        this.complementRepository = complementRepository;
        this.imageService = imageService;
    }
    
    @Override
    public Complement create(Complement complement, File imageFile) {
        if (!validateComplement(complement)) {
            System.err.println("❌ Validation complément échouée");
            return null;
        }
        
        if (imageFile != null && imageFile.exists()) {
            String imageUrl = imageService.uploadImage(imageFile);
            if (imageUrl != null) {
                complement.setImage(imageUrl);
            } else {
                System.err.println("⚠️  Upload image échoué, création complément sans image");
            }
        }
        
        Complement created = complementRepository.create(complement);
        
        if (created != null) {
            System.out.println("✅ Complément créé avec succès : " + created.getNom());
        }
        
        return created;
    }
    
    @Override
    public List<Complement> findAll() {
        return complementRepository.findAll();
    }
    
    @Override
    public List<Complement> findAvailable() {
        return complementRepository.findByArchiveStatus(false);
    }
    
    @Override
    public Complement findById(Integer id) {
        if (id == null || id <= 0) {
            System.err.println("❌ ID invalide");
            return null;
        }
        
        return complementRepository.findById(id);
    }
    
    @Override
    public boolean update(Complement complement, File newImageFile) {
        if (!validateComplement(complement)) {
            System.err.println("❌ Validation complément échouée");
            return false;
        }
        
        if (complement.getId() == null) {
            System.err.println("❌ ID complément requis pour la mise à jour");
            return false;
        }
        
        Complement existingComplement = complementRepository.findById(complement.getId());
        if (existingComplement == null) {
            System.err.println("❌ Complément introuvable avec l'ID : " + complement.getId());
            return false;
        }
        
        if (newImageFile != null && newImageFile.exists()) {
            if (existingComplement.getImage() != null && !existingComplement.getImage().isEmpty()) {
                imageService.deleteImage(existingComplement.getImage());
            }
            
            String newImageUrl = imageService.uploadImage(newImageFile);
            if (newImageUrl != null) {
                complement.setImage(newImageUrl);
            } else {
                complement.setImage(existingComplement.getImage());
            }
        } else {
            complement.setImage(existingComplement.getImage());
        }
        
        boolean updated = complementRepository.update(complement);
        
        if (updated) {
            System.out.println("✅ Complément mis à jour avec succès");
        }
        
        return updated;
    }
    
    @Override
    public boolean archive(Integer id) {
        if (id == null || id <= 0) {
            System.err.println("❌ ID invalide");
            return false;
        }
        
        Complement complement = complementRepository.findById(id);
        if (complement == null) {
            System.err.println("❌ Complément introuvable");
            return false;
        }
        
        boolean archived = complementRepository.archive(id);
        
        if (archived) {
            System.out.println("✅ Complément archivé avec succès");
        }
        
        return archived;
    }
    
    @Override
    public List<Complement> findByType(TypeComplement type) {
        if (type == null) {
            System.err.println("❌ Type requis");
            return null;
        }
        
        return complementRepository.findByType(type);
    }
    
    @Override
    public boolean validateComplement(Complement complement) {
        if (complement == null) {
            System.err.println("❌ Complément null");
            return false;
        }
        
        if (complement.getNom() == null || complement.getNom().trim().isEmpty()) {
            System.err.println("❌ Nom du complément requis");
            return false;
        }
        
        if (complement.getNom().length() < 3 || complement.getNom().length() > 100) {
            System.err.println("❌ Nom du complément doit contenir entre 3 et 100 caractères");
            return false;
        }
        
        if (complement.getType() == null) {
            System.err.println("❌ Type du complément requis (BOISSON ou FRITES)");
            return false;
        }
        
        if (complement.getPrix() == null) {
            System.err.println("❌ Prix du complément requis");
            return false;
        }
        
        if (complement.getPrix().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            System.err.println("❌ Le prix doit être supérieur à 0");
            return false;
        }
        
        if (complement.getPrix().compareTo(new java.math.BigDecimal("50000")) > 0) {
            System.err.println("❌ Le prix ne peut pas dépasser 50 000 FCFA");
            return false;
        }
        
        return true;
    }
}