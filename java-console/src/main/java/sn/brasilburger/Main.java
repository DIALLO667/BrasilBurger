package sn.brasilburger;

import sn.brasilburger.config.DatabaseConfig;
import sn.brasilburger.entity.Burger;
import sn.brasilburger.repository.BurgerRepository;
import sn.brasilburger.repository.impl.BurgerRepositoryImpl;

import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TEST BURGER REPOSITORY ===\n");
        
        BurgerRepository burgerRepo = new BurgerRepositoryImpl();
        
        // Test 1 : Récupérer tous les burgers
        System.out.println("--- Test 1 : findAll() ---");
        List<Burger> burgers = burgerRepo.findAll();
        for (Burger b : burgers) {
            System.out.println(b);
        }
        
        System.out.println("\n--- Test 2 : findById(1) ---");
        Burger burger = burgerRepo.findById(1);
        if (burger != null) {
            System.out.println(burger);
        }
        
        System.out.println("\n--- Test 3 : findByPriceRange(2000, 3500) ---");
        List<Burger> burgersParPrix = burgerRepo.findByPriceRange(
            new BigDecimal("2000"),
            new BigDecimal("3500")
        );
        for (Burger b : burgersParPrix) {
            System.out.println(b);
        }
        
        System.out.println("\n--- Test 4 : findByArchiveStatus(false) ---");
        List<Burger> burgersActifs = burgerRepo.findByArchiveStatus(false);
        System.out.println("Nombre de burgers actifs : " + burgersActifs.size());
        
        DatabaseConfig.closeConnection();
        System.out.println("\n=== TESTS TERMINES ===");
    }
}