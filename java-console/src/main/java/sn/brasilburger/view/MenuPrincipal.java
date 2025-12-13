package sn.brasilburger.view;

import sn.brasilburger.config.DatabaseConfig;
import java.util.Scanner;

public class MenuPrincipal {
    
    private BurgerVue burgerVue;
    private ComplementVue complementVue;
    private MenuVue menuVue;
    private Scanner scanner;
    
    public MenuPrincipal() {
        this.burgerVue = new BurgerVue();
        this.complementVue = new ComplementVue();
        this.menuVue = new MenuVue();
        this.scanner = new Scanner(System.in);
    }
    
    public void afficher() {
        boolean continuer = true;
        
        afficherBanniere();
        
        while (continuer) {
            afficherMenu();
            int choix = lireChoix();
            
            System.out.println();
            
            switch (choix) {
                case 1:
                    burgerVue.afficher();
                    break;
                case 2:
                    complementVue.afficher();
                    break;
                case 3:
                    menuVue.afficher();
                    break;
                case 0:
                    continuer = false;
                    quitter();
                    break;
                default:
                    System.out.println("❌ Choix invalide !");
            }
        }
    }
    
    private void afficherBanniere() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║                                                ║");
        System.out.println("║        🍔  BRASIL BURGER  🍔                  ║");
        System.out.println("║                                                ║");
        System.out.println("║        Gestion du Catalogue                    ║");
        System.out.println("║                                                ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    private void afficherMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         MENU PRINCIPAL               ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. Gestion des Burgers              ║");
        System.out.println("║  2. Gestion des Compléments          ║");
        System.out.println("║  3. Gestion des Menus                ║");
        System.out.println("║  0. Quitter                          ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("\nVotre choix : ");
    }
    
    private void quitter() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║      Merci d'avoir utilisé           ║");
        System.out.println("║         BRASIL BURGER                ║");
        System.out.println("║                                      ║");
        System.out.println("║         À bientôt ! 🍔               ║");
        System.out.println("╚══════════════════════════════════════╝\n");
        
        DatabaseConfig.closeConnection();
    }
    
    private int lireChoix() {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}