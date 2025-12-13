package sn.brasilburger.view;

import sn.brasilburger.entity.Menu;
import sn.brasilburger.entity.Burger;
import sn.brasilburger.entity.Complement;
import sn.brasilburger.entity.enums.TypeComplement;
import sn.brasilburger.service.MenuService;
import sn.brasilburger.service.BurgerService;
import sn.brasilburger.service.ComplementService;
import sn.brasilburger.service.impl.MenuServiceImpl;
import sn.brasilburger.service.impl.BurgerServiceImpl;
import sn.brasilburger.service.impl.ComplementServiceImpl;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class MenuVue {
    
    private MenuService menuService;
    private BurgerService burgerService;
    private ComplementService complementService;
    private Scanner scanner;
    
    public MenuVue() {
        this.menuService = new MenuServiceImpl();
        this.burgerService = new BurgerServiceImpl();
        this.complementService = new ComplementServiceImpl();
        this.scanner = new Scanner(System.in);
    }
    
    public void afficher() {
        boolean continuer = true;
        
        while (continuer) {
            afficherMenu();
            int choix = lireChoix();
            
            switch (choix) {
                case 1:
                    creer();
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
        System.out.println("║        GESTION DES MENUS             ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. Créer un menu                    ║");
        System.out.println("║  2. Lister les menus                 ║");
        System.out.println("║  3. Modifier un menu                 ║");
        System.out.println("║  4. Archiver un menu                 ║");
        System.out.println("║  0. Retour                           ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("\nVotre choix : ");
    }
    
    private void creer() {
        System.out.println("\n═══ CREER UN MENU ═══");
        
        System.out.print("Nom du menu : ");
        String nom = scanner.nextLine();
        
        System.out.println("\n--- Sélection du Burger ---");
        List<Burger> burgers = burgerService.findAvailable();
        if (burgers.isEmpty()) {
            System.out.println("❌ Aucun burger disponible");
            return;
        }
        
        for (Burger burger : burgers) {
            System.out.println(burger.getId() + ". " + burger.getNom() + " (" + burger.getPrix() + " FCFA)");
        }
        System.out.print("ID du burger : ");
        int burgerId = lireChoix();
        
        System.out.println("\n--- Sélection de la Boisson ---");
        List<Complement> boissons = complementService.findByType(TypeComplement.BOISSON);
        if (boissons.isEmpty()) {
            System.out.println("❌ Aucune boisson disponible");
            return;
        }
        
        for (Complement boisson : boissons) {
            System.out.println(boisson.getId() + ". " + boisson.getNom() + " (" + boisson.getPrix() + " FCFA)");
        }
        System.out.print("ID de la boisson : ");
        int boissonId = lireChoix();
        
        System.out.println("\n--- Sélection des Frites ---");
        List<Complement> frites = complementService.findByType(TypeComplement.FRITES);
        if (frites.isEmpty()) {
            System.out.println("❌ Aucunes frites disponibles");
            return;
        }
        
        for (Complement frite : frites) {
            System.out.println(frite.getId() + ". " + frite.getNom() + " (" + frite.getPrix() + " FCFA)");
        }
        System.out.print("ID des frites : ");
        int fritesId = lireChoix();
        
        System.out.print("\nChemin de l'image du menu (vide pour ignorer) : ");
        String imagePath = scanner.nextLine();
        
        Menu menu = new Menu(nom, burgerId, boissonId, fritesId);
        
        File imageFile = null;
        if (!imagePath.trim().isEmpty()) {
            imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.out.println("⚠️  Image non trouvée, création sans image");
                imageFile = null;
            }
        }
        
        Menu created = menuService.create(menu, imageFile);
        
        if (created != null) {
            System.out.println("\n✅ Menu créé avec succès !");
            System.out.println("   ID : " + created.getId());
            System.out.println("   Nom : " + created.getNom());
            System.out.println("   Prix total : " + created.getPrixCalcule() + " FCFA");
        } else {
            System.out.println("\n❌ Échec de la création du menu");
        }
    }
    
    private void lister() {
        System.out.println("\n═══ LISTE DES MENUS ═══");
        
        List<Menu> menus = menuService.findAllWithDetails();
        
        if (menus.isEmpty()) {
            System.out.println("\n⚠️  Aucun menu trouvé");
            return;
        }
        
        System.out.println("\n┌──────┬────────────────────────┬───────────┐");
        System.out.println("│  ID  │         Nom            │   Prix    │");
        System.out.println("├──────┼────────────────────────┼───────────┤");
        
        for (Menu menu : menus) {
            System.out.printf("│ %-4d │ %-22s │ %9s │%n",
                menu.getId(),
                truncate(menu.getNom(), 22),
                menu.getPrixCalcule()
            );
        }
        
        System.out.println("└──────┴────────────────────────┴───────────┘");
        
        System.out.print("\nVoir le détail d'un menu ? (ID ou 0) : ");
        int id = lireChoix();
        
        if (id > 0) {
            afficherDetail(id);
        }
    }
    
    private void afficherDetail(int id) {
        Menu menu = menuService.findByIdWithDetails(id);
        
        if (menu == null) {
            System.out.println("❌ Menu introuvable");
            return;
        }
        
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║          DETAIL DU MENU              ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("  Nom : " + menu.getNom());
        System.out.println("  Burger : " + menu.getBurger().getNom() + " (" + menu.getBurger().getPrix() + " FCFA)");
        System.out.println("  Boisson : " + menu.getBoisson().getNom() + " (" + menu.getBoisson().getPrix() + " FCFA)");
        System.out.println("  Frites : " + menu.getFrites().getNom() + " (" + menu.getFrites().getPrix() + " FCFA)");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("  PRIX TOTAL : " + menu.getPrixCalcule() + " FCFA");
        System.out.println("╚══════════════════════════════════════╝");
    }
    
    private void modifier() {
        System.out.println("\n═══ MODIFIER UN MENU ═══");
        
        System.out.print("ID du menu à modifier : ");
        int id = lireChoix();
        
        Menu menu = menuService.findByIdWithDetails(id);
        if (menu == null) {
            System.out.println("❌ Menu introuvable");
            return;
        }
        
        afficherDetail(id);
        
        System.out.print("\nNouveau nom (vide pour garder) : ");
        String nom = scanner.nextLine();
        if (!nom.trim().isEmpty()) {
            menu.setNom(nom);
        }
        
        System.out.print("Modifier les composants ? (O/N) : ");
        String modifierComposants = scanner.nextLine();
        
        if (modifierComposants.equalsIgnoreCase("O") || modifierComposants.equalsIgnoreCase("OUI")) {
            System.out.print("Nouvel ID burger (vide pour garder) : ");
            String burgerIdStr = scanner.nextLine();
            if (!burgerIdStr.trim().isEmpty()) {
                menu.setBurgerId(Integer.parseInt(burgerIdStr));
            }
            
            System.out.print("Nouvel ID boisson (vide pour garder) : ");
            String boissonIdStr = scanner.nextLine();
            if (!boissonIdStr.trim().isEmpty()) {
                menu.setBoissonId(Integer.parseInt(boissonIdStr));
            }
            
            System.out.print("Nouvel ID frites (vide pour garder) : ");
            String fritesIdStr = scanner.nextLine();
            if (!fritesIdStr.trim().isEmpty()) {
                menu.setFritesId(Integer.parseInt(fritesIdStr));
            }
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
        
        boolean updated = menuService.update(menu, imageFile);
        
        if (updated) {
            System.out.println("\n✅ Menu modifié avec succès !");
            System.out.println("   Nouveau prix : " + menu.getPrixCalcule() + " FCFA");
        } else {
            System.out.println("\n❌ Échec de la modification");
        }
    }
    
    private void archiver() {
        System.out.println("\n═══ ARCHIVER UN MENU ═══");
        
        System.out.print("ID du menu à archiver : ");
        int id = lireChoix();
        
        Menu menu = menuService.findById(id);
        if (menu == null) {
            System.out.println("❌ Menu introuvable");
            return;
        }
        
        System.out.println("\nMenu à archiver : " + menu.getNom());
        System.out.print("Confirmer l'archivage ? (O/N) : ");
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("O") || confirmation.equalsIgnoreCase("OUI")) {
            boolean archived = menuService.archive(id);
            
            if (archived) {
                System.out.println("\n✅ Menu archivé avec succès !");
            } else {
                System.out.println("\n❌ Échec de l'archivage");
            }
        } else {
            System.out.println("\n⚠️  Archivage annulé");
        }
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