package sn.brasilburger.repository;

import sn.brasilburger.entity.Burger;
import java.math.BigDecimal;
import java.util.List;

public interface BurgerRepository {
    
    Burger create(Burger burger);
    
    List<Burger> findAll();
    
    Burger findById(Integer id);
    
    boolean update(Burger burger);
    
    boolean archive(Integer id);
    
    List<Burger> findByArchiveStatus(boolean archive);
    
    List<Burger> findByPriceRange(BigDecimal minPrix, BigDecimal maxPrix);
}