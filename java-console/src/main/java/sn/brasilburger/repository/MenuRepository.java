package sn.brasilburger.repository;

import sn.brasilburger.entity.Menu;
import java.util.List;

public interface MenuRepository {
    
    Menu create(Menu menu);
    
    List<Menu> findAll();
    
    Menu findById(Integer id);
    
    boolean update(Menu menu);
    
    boolean archive(Integer id);
    
    List<Menu> findByArchiveStatus(boolean archive);
    
    Menu findByIdWithDetails(Integer id);
    
    List<Menu> findAllWithDetails();
}