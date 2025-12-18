package sn.brasilburger.view;

import sn.brasilburger.entity.Burger;
import sn.brasilburger.service.BurgerService;
import sn.brasilburger.service.impl.BurgerServiceImpl;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class BurgerVue {
    
    private BurgerService burgerService;
    private Scanner scanner;
    
    public BurgerVue() {
        this.burgerService = new BurgerServiceImpl();
        this.scanner = new Scanner(System.in);
    }
    
    public void afficher() {
        boolean continuer = true;
        
        while (continuer) {
            afficherMenu();
            int choix = lireChoix();
            
            switch (choix) {
                case 1:
                    ajouter();
                    break;
                case 2:
                    lister();
                    break;
                case 3:
                    modifier();
                    break;
                case 4:
                    archiver();
                    break;
                case 5:
                    rechercherParPrix();
                    break;
                case 0:
                    continuer = false;
                    System.out.println("Retour au menu principal...");
                    break;
                default:
                    System.out.println("❌ Choix invalide !");
            }
            
            if (continuer) {
                System.out.println("\nAppuyez sur Entrée pour continuer...");
                scanner.nextLine();
            }
        }
    }
    
    private void afficherMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║       GESTION DES BURGERS            ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. Ajouter un burger                ║");
        System.out.println("║  2. Lister les burgers               ║");
        System.out.println("║  3. Modifier un burger               ║");
        System.out.println("║  4. Archiver un burger               ║");
        System.out.println("║  5. Rechercher par prix              ║");
        System.out.println("║  0. Retour                           ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("\nVotre choix : ");
    }
    
    private void ajouter() {
        System.out.println("\n═══ AJOUTER UN BURGER ═══");
        
        System.out.print("Nom du burger : ");
        String nom = scanner.nextLine();
        
        System.out.print("Prix (FCFA) : ");
        BigDecimal prix = new BigDecimal(scanner.nextLine());
        
        System.out.print("Description : ");
        String description = scanner.nextLine();
        
        System.out.print("Chemin de l'image (vide pour ignorer) : ");
        String imagePath = scanner.nextLine();
        
        Burger burger = new Burger(nom, prix, description);
        
        File imageFile = null;
        if (!imagePath.trim().isEmpty()) {
            imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.out.println("⚠️  Image non trouvée, création sans image");
                imageFile = null;
            }
        }
        
        Burger created = burgerService.create(burger, imageFile);
        
        if (created != null) {
            System.out.println("\n✅ Burger ajouté avec succès !");
            System.out.println("   ID : " + created.getId());
            System.out.println("   Nom : " + created.getNom());
            System.out.println("   Prix : " + created.getPrix() + " FCFA");
        } else {
            System.out.println("\n❌ Échec de l'ajout du burger");
        }
    }
    
    private void lister() {
        System.out.println("\n═══ LISTE DES BURGERS ═══");
        
        System.out.println("1. Tous les burgers");
        System.out.println("2. Burgers actifs seulement");
        System.out.print("Votre choix : ");
        
        int choix = lireChoix();
        
        List<Burger> burgers;
        if (choix == 2) {
            burgers = burgerService.findAvailable();
        } else {
            burgers = burgerService.findAll();
        }
        
        if (burgers.isEmpty()) {
            System.out.println("\n⚠️  Aucun burger trouvé");
            return;
        }
        
        System.out.println("\n┌──────┬────────────────────────┬───────────┬──────────┐");
        System.out.println("│  ID  │         Nom            │   Prix    │  Statut  │");
        System.out.println("├──────┼────────────────────────┼───────────┼──────────┤");
        
        for (Burger burger : burgers) {
            String statut = burger.getArchive() ? "Archivé" : "Actif";
            System.out.printf("│ %-4d │ %-22s │ %9s │ %-8s │%n",
                burger.getId(),
                truncate(burger.getNom(), 22),
                burger.getPrix(),
                statut
            );
        }
        
        System.out.println("└──────┴────────────────────────┴───────────┴──────────┘");
        System.out.println("\nTotal : " + burgers.size() + " burger(s)");
    }
    
    private void modifier() {
        System.out.println("\n═══ MODIFIER UN BURGER ═══");
        
        System.out.print("ID du burger à modifier : ");
        int id = lireChoix();
        
        Burger burger = burgerService.findById(id);
        if (burger == null) {
            System.out.println("❌ Burger introuvable");
            return;
        }
        
        System.out.println("\nBurger actuel :");
        System.out.println("  Nom : " + burger.getNom());
        System.out.println("  Prix : " + burger.getPrix() + " FCFA");
        System.out.println("  Description : " + burger.getDescription());
        
        System.out.print("\nNouveau nom (vide pour garder) : ");
        String nom = scanner.nextLine();
        if (!nom.trim().isEmpty()) {
            burger.setNom(nom);
        }
        
        System.out.print("Nouveau prix (vide pour garder) : ");
        String prixStr = scanner.nextLine();
        if (!prixStr.trim().isEmpty()) {
            burger.setPrix(new BigDecimal(prixStr));
        }
        
        System.out.print("Nouvelle description (vide pour garder) : ");
        String description = scanner.nextLine();
        if (!description.trim().isEmpty()) {
            burger.setDescription(description);
        }
        
        System.out.print("Nouvelle image (vide pour garder) : ");
        String imagePath = scanner.nextLine();
        
        File imageFile = null;
        if (!imagePath.trim().isEmpty()) {
            imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.out.println("⚠️  Image non trouvée, ancienne image conservée");
                imageFile = null;
            }
        }
        
        boolean updated = burgerService.update(burger, imageFile);
        
        if (updated) {
            System.out.println("\n✅ Burger modifié avec succès !");
        } else {
            System.out.println("\n❌ Échec de la modification");
        }
    }
    
    private void archiver() {
        System.out.println("\n═══ ARCHIVER UN BURGER ═══");
        
        System.out.print("ID du burger à archiver : ");
        int id = lireChoix();
        
        Burger burger = burgerService.findById(id);
        if (burger == null) {
            System.out.println("❌ Burger introuvable");
            return;
        }
        
        System.out.println("\nBurger à archiver : " + burger.getNom());
        System.out.print("Confirmer l'archivage ? (O/N) : ");
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("O") || confirmation.equalsIgnoreCase("OUI")) {
            boolean archived = burgerService.archive(id);
            
            if (archived) {
                System.out.println("\n✅ Burger archivé avec succès !");
            } else {
                System.out.println("\n❌ Échec de l'archivage");
            }
        } else {
            System.out.println("\n⚠️  Archivage annulé");
        }
    }
    
    private void rechercherParPrix() {
        System.out.println("\n═══ RECHERCHE PAR PRIX ═══");
        
        System.out.print("Prix minimum (FCFA) : ");
        BigDecimal minPrix = new BigDecimal(scanner.nextLine());
        
        System.out.print("Prix maximum (FCFA) : ");
        BigDecimal maxPrix = new BigDecimal(scanner.nextLine());
        
        List<Burger> burgers = burgerService.findByPriceRange(minPrix, maxPrix);
        
        if (burgers == null || burgers.isEmpty()) {
            System.out.println("\n⚠️  Aucun burger trouvé dans cette fourchette de prix");
            return;
        }
        
        System.out.println("\n┌──────┬────────────────────────┬───────────┐");
        System.out.println("│  ID  │         Nom            │   Prix    │");
        System.out.println("├──────┼────────────────────────┼───────────┤");
        
        for (Burger burger : burgers) {
            System.out.printf("│ %-4d │ %-22s │ %9s │%n",
                burger.getId(),
                truncate(burger.getNom(), 22),
                burger.getPrix()
            );
        }
        
        System.out.println("└──────┴────────────────────────┴───────────┘");
        System.out.println("\nTotal : " + burgers.size() + " burger(s)");
    }
    
    private int lireChoix() {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}