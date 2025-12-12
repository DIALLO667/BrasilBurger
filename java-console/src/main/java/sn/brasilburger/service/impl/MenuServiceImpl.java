package sn.brasilburger.service.impl;

import sn.brasilburger.entity.Menu;
import sn.brasilburger.entity.Burger;
import sn.brasilburger.entity.Complement;
import sn.brasilburger.repository.MenuRepository;
import sn.brasilburger.repository.BurgerRepository;
import sn.brasilburger.repository.ComplementRepository;
import sn.brasilburger.repository.impl.MenuRepositoryImpl;
import sn.brasilburger.repository.impl.BurgerRepositoryImpl;
import sn.brasilburger.repository.impl.ComplementRepositoryImpl;
import sn.brasilburger.service.MenuService;
import sn.brasilburger.service.ImageService;

import java.io.File;
import java.util.List;

public class MenuServiceImpl implements MenuService {
    
    private MenuRepository menuRepository;
    private BurgerRepository burgerRepository;
    private ComplementRepository complementRepository;
    private ImageService imageService;
    
    public MenuServiceImpl() {
        this.menuRepository = new MenuRepositoryImpl();
        this.burgerRepository = new BurgerRepositoryImpl();
        this.complementRepository = new ComplementRepositoryImpl();
        this.imageService = new CloudinaryImageService();
    }
    
    public MenuServiceImpl(MenuRepository menuRepository, 
                          BurgerRepository burgerRepository,
                          ComplementRepository complementRepository,
                          ImageService imageService) {
        this.menuRepository = menuRepository;
        this.burgerRepository = burgerRepository;
        this.complementRepository = complementRepository;
        this.imageService = imageService;
    }
    
    @Override
    public Menu create(Menu menu, File imageFile) {
        if (!validateMenu(menu)) {
            System.err.println("❌ Validation menu échouée");
            return null;
        }
        
        if (imageFile != null && imageFile.exists()) {
            String imageUrl = imageService.uploadImage(imageFile);
            if (imageUrl != null) {
                menu.setImage(imageUrl);
            } else {
                System.err.println("⚠️  Upload image échoué, création menu sans image");
            }
        }
        
        Menu created = menuRepository.create(menu);
        
        if (created != null) {
            System.out.println("✅ Menu créé avec succès : " + created.getNom());
            System.out.println("   Prix calculé : " + created.getPrixCalcule() + " FCFA");
        }
        
        return created;
    }
    
    @Override
    public List<Menu> findAll() {
        return menuRepository.findAll();
    }
    
    @Override
    public List<Menu> findAvailable() {
        return menuRepository.findByArchiveStatus(false);
    }
    
    @Override
    public List<Menu> findAllWithDetails() {
        return menuRepository.findAllWithDetails();
    }
    
    @Override
    public Menu findById(Integer id) {
        if (id == null || id <= 0) {
            System.err.println("❌ ID invalide");
            return null;
        }
        
        return menuRepository.findById(id);
    }
    
    @Override
    public Menu findByIdWithDetails(Integer id) {
        if (id == null || id <= 0) {
            System.err.println("❌ ID invalide");
            return null;
        }
        
        return menuRepository.findByIdWithDetails(id);
    }
    
    @Override
    public boolean update(Menu menu, File newImageFile) {
        if (!validateMenu(menu)) {
            System.err.println("❌ Validation menu échouée");
            return false;
        }
        
        if (menu.getId() == null) {
            System.err.println("❌ ID menu requis pour la mise à jour");
            return false;
        }
        
        Menu existingMenu = menuRepository.findById(menu.getId());
        if (existingMenu == null) {
            System.err.println("❌ Menu introuvable avec l'ID : " + menu.getId());
            return false;
        }
        
        if (newImageFile != null && newImageFile.exists()) {
            if (existingMenu.getImage() != null && !existingMenu.getImage().isEmpty()) {
                imageService.deleteImage(existingMenu.getImage());
            }
            
            String newImageUrl = imageService.uploadImage(newImageFile);
            if (newImageUrl != null) {
                menu.setImage(newImageUrl);
            } else {
                menu.setImage(existingMenu.getImage());
            }
        } else {
            menu.setImage(existingMenu.getImage());
        }
        
        boolean updated = menuRepository.update(menu);
        
        if (updated) {
            System.out.println("✅ Menu mis à jour avec succès");
            System.out.println("   Nouveau prix : " + menu.getPrixCalcule() + " FCFA");
        }
        
        return updated;
    }
    
    @Override
    public boolean archive(Integer id) {
        if (id == null || id <= 0) {
            System.err.println("❌ ID invalide");
            return false;
        }
        
        Menu menu = menuRepository.findById(id);
        if (menu == null) {
            System.err.println("❌ Menu introuvable");
            return false;
        }
        
        boolean archived = menuRepository.archive(id);
        
        if (archived) {
            System.out.println("✅ Menu archivé avec succès");
        }
        
        return archived;
    }
    
    @Override
    public boolean validateMenu(Menu menu) {
        if (menu == null) {
            System.err.println("❌ Menu null");
            return false;
        }
        
        if (menu.getNom() == null || menu.getNom().trim().isEmpty()) {
            System.err.println("❌ Nom du menu requis");
            return false;
        }
        
        if (menu.getNom().length() < 3 || menu.getNom().length() > 100) {
            System.err.println("❌ Nom du menu doit contenir entre 3 et 100 caractères");
            return false;
        }
        
        if (menu.getBurgerId() == null) {
            System.err.println("❌ Burger requis pour le menu");
            return false;
        }
        
        Burger burger = burgerRepository.findById(menu.getBurgerId());
        if (burger == null) {
            System.err.println("❌ Burger introuvable avec l'ID : " + menu.getBurgerId());
            return false;
        }
        
        if (menu.getBoissonId() == null) {
            System.err.println("❌ Boisson requise pour le menu");
            return false;
        }
        
        Complement boisson = complementRepository.findById(menu.getBoissonId());
        if (boisson == null) {
            System.err.println("❌ Boisson introuvable avec l'ID : " + menu.getBoissonId());
            return false;
        }
        
        if (menu.getFritesId() == null) {
            System.err.println("❌ Frites requises pour le menu");
            return false;
        }
        
        Complement frites = complementRepository.findById(menu.getFritesId());
        if (frites == null) {
            System.err.println("❌ Frites introuvables avec l'ID : " + menu.getFritesId());
            return false;
        }
        
        return true;
    }
}