package sn.brasilburger.service;

import sn.brasilburger.entity.Burger;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public interface BurgerService {
    
    Burger create(Burger burger, File imageFile);
    
    List<Burger> findAll();
    
    List<Burger> findAvailable();
    
    Burger findById(Integer id);
    
    boolean update(Burger burger, File newImageFile);
    
    boolean archive(Integer id);
    
    List<Burger> findByPriceRange(BigDecimal minPrix, BigDecimal maxPrix);
    
    boolean validateBurger(Burger burger);
}