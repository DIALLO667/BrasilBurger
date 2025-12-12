package sn.brasilburger;

import sn.brasilburger.config.DatabaseConfig;
import sn.brasilburger.entity.Complement;
import sn.brasilburger.entity.enums.TypeComplement;
import sn.brasilburger.repository.ComplementRepository;
import sn.brasilburger.repository.impl.ComplementRepositoryImpl;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TEST COMPLEMENT REPOSITORY ===\n");
        
        ComplementRepository complementRepo = new ComplementRepositoryImpl();
        
        // Test 1 : Récupérer tous les compléments
        System.out.println("--- Test 1 : findAll() ---");
        List<Complement> complements = complementRepo.findAll();
        for (Complement c : complements) {
            System.out.println(c);
        }
        
        System.out.println("\n--- Test 2 : findById(1) ---");
        Complement complement = complementRepo.findById(1);
        if (complement != null) {
            System.out.println(complement);
        }
        
        System.out.println("\n--- Test 3 : findByType(BOISSON) ---");
        List<Complement> boissons = complementRepo.findByType(TypeComplement.BOISSON);
        System.out.println("Nombre de boissons : " + boissons.size());
        for (Complement c : boissons) {
            System.out.println("  - " + c.getNom() + " (" + c.getPrix() + " FCFA)");
        }
        
        System.out.println("\n--- Test 4 : findByType(FRITES) ---");
        List<Complement> frites = complementRepo.findByType(TypeComplement.FRITES);
        System.out.println("Nombre de frites : " + frites.size());
        for (Complement c : frites) {
            System.out.println("  - " + c.getNom() + " (" + c.getPrix() + " FCFA)");
        }
        
        System.out.println("\n--- Test 5 : findByArchiveStatus(false) ---");
        List<Complement> complementsActifs = complementRepo.findByArchiveStatus(false);
        System.out.println("Nombre de compléments actifs : " + complementsActifs.size());
        
        DatabaseConfig.closeConnection();
        System.out.println("\n=== TESTS TERMINES ===");
    }
}