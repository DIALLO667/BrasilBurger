package sn.brasilburger.service;

import sn.brasilburger.entity.Menu;
import java.io.File;
import java.util.List;

public interface MenuService {
    
    Menu create(Menu menu, File imageFile);
    
    List<Menu> findAll();
    
    List<Menu> findAvailable();
    
    List<Menu> findAllWithDetails();
    
    Menu findById(Integer id);
    
    Menu findByIdWithDetails(Integer id);
    
    boolean update(Menu menu, File newImageFile);
    
    boolean archive(Integer id);
    
    boolean validateMenu(Menu menu);
}