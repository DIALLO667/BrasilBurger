package sn.brasilburger.service.impl;

import sn.brasilburger.entity.Burger;
import sn.brasilburger.repository.BurgerRepository;
import sn.brasilburger.repository.impl.BurgerRepositoryImpl;
import sn.brasilburger.service.BurgerService;
import sn.brasilburger.service.ImageService;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class BurgerServiceImpl implements BurgerService {
    
    private BurgerRepository burgerRepository;
    private ImageService imageService;
    
    public BurgerServiceImpl() {
        this.burgerRepository = new BurgerRepositoryImpl();
        this.imageService = new CloudinaryImageService();
    }
    
    public BurgerServiceImpl(BurgerRepository burgerRepository, ImageService imageService) {
        this.burgerRepository = burgerRepository;
        this.imageService = imageService;
    }
    
    @Override
    public Burger create(Burger burger, File imageFile) {
        if (!validateBurger(burger)) {
            System.err.println(" Validation burger échouée");
            return null;
        }
        
        if (imageFile != null && imageFile.exists()) {
            String imageUrl = imageService.uploadImage(imageFile);
            if (imageUrl != null) {
                burger.setImage(imageUrl);
            } else {
                System.err.println("⚠️  Upload image échoué, création burger sans image");
            }
        }
        
        Burger created = burgerRepository.create(burger);
        
        if (created != null) {
            System.out.println("✅ Burger créé avec succès : " + created.getNom());
        }
        
        return created;
    }
    
    @Override
    public List<Burger> findAll() {
        return burgerRepository.findAll();
    }
    
    @Override
    public List<Burger> findAvailable() {
        return burgerRepository.findByArchiveStatus(false);
    }
    
    @Override
    public Burger findById(Integer id) {
        if (id == null || id <= 0) {
            System.err.println(" ID invalide");
            return null;
        }
        
        return burgerRepository.findById(id);
    }
    
    @Override
    public boolean update(Burger burger, File newImageFile) {
        if (!validateBurger(burger)) {
            System.err.println(" Validation burger échouée");
            return false;
        }
        
        if (burger.getId() == null) {
            System.err.println(" ID burger requis pour la mise à jour");
            return false;
        }
        
        Burger existingBurger = burgerRepository.findById(burger.getId());
        if (existingBurger == null) {
            System.err.println(" Burger introuvable avec l'ID : " + burger.getId());
            return false;
        }
        
        if (newImageFile != null && newImageFile.exists()) {
            if (existingBurger.getImage() != null && !existingBurger.getImage().isEmpty()) {
                imageService.deleteImage(existingBurger.getImage());
            }
            
            String newImageUrl = imageService.uploadImage(newImageFile);
            if (newImageUrl != null) {
                burger.setImage(newImageUrl);
            } else {
                burger.setImage(existingBurger.getImage());
            }
        } else {
            burger.setImage(existingBurger.getImage());
        }
        
        boolean updated = burgerRepository.update(burger);
        
        if (updated) {
            System.out.println("✅ Burger mis à jour avec succès");
        }
        
        return updated;
    }
    
    @Override
    public boolean archive(Integer id) {
        if (id == null || id <= 0) {
            System.err.println("❌ ID invalide");
            return false;
        }
        
        Burger burger = burgerRepository.findById(id);
        if (burger == null) {
            System.err.println("❌ Burger introuvable");
            return false;
        }
        
        boolean archived = burgerRepository.archive(id);
        
        if (archived) {
            System.out.println("✅ Burger archivé avec succès");
        }
        
        return archived;
    }
    
    @Override
    public List<Burger> findByPriceRange(BigDecimal minPrix, BigDecimal maxPrix) {
        if (minPrix == null || maxPrix == null) {
            System.err.println("❌ Prix min et max requis");
            return null;
        }
        
        if (minPrix.compareTo(BigDecimal.ZERO) < 0 || maxPrix.compareTo(BigDecimal.ZERO) < 0) {
            System.err.println("❌ Les prix doivent être positifs");
            return null;
        }
        
        if (minPrix.compareTo(maxPrix) > 0) {
            System.err.println("❌ Le prix min doit être inférieur au prix max");
            return null;
        }
        
        return burgerRepository.findByPriceRange(minPrix, maxPrix);
    }
    
    @Override
    public boolean validateBurger(Burger burger) {
        if (burger == null) {
            System.err.println("❌ Burger null");
            return false;
        }
        
        if (burger.getNom() == null || burger.getNom().trim().isEmpty()) {
            System.err.println("❌ Nom du burger requis");
            return false;
        }
        
        if (burger.getNom().length() < 3 || burger.getNom().length() > 100) {
            System.err.println("❌ Nom du burger doit contenir entre 3 et 100 caractères");
            return false;
        }
        
        if (burger.getPrix() == null) {
            System.err.println("❌ Prix du burger requis");
            return false;
        }
        
        if (burger.getPrix().compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println(" Le prix doit être supérieur à 0");
            return false;
        }
        
        if (burger.getPrix().compareTo(new BigDecimal("100000")) > 0) {
            System.err.println(" Le prix ne peut pas dépasser 100 000 FCFA");
            return false;
        }
        
        return true;
    }
}