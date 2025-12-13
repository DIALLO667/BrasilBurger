package sn.brasilburger.view;

import sn.brasilburger.entity.Complement;
import sn.brasilburger.entity.enums.TypeComplement;
import sn.brasilburger.service.ComplementService;
import sn.brasilburger.service.impl.ComplementServiceImpl;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ComplementVue {
    
    private ComplementService complementService;
    private Scanner scanner;
    
    public ComplementVue() {
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
                    ajouter();
                    break;
                case 2:
                    listerParType(TypeComplement.BOISSON);
                    break;
                case 3:
                    listerParType(TypeComplement.FRITES);
                    break;
                case 4:
                    listerTous();
                    break;
                case 5:
                    modifier();
                    break;
                case 6:
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
        System.out.println("║     GESTION DES COMPLEMENTS          ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. Ajouter un complément            ║");
        System.out.println("║  2. Lister les boissons              ║");
        System.out.println("║  3. Lister les frites                ║");
        System.out.println("║  4. Lister tous                      ║");
        System.out.println("║  5. Modifier un complément           ║");
        System.out.println("║  6. Archiver un complément           ║");
        System.out.println("║  0. Retour                           ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("\nVotre choix : ");
    }
    
    private void ajouter() {
        System.out.println("\n═══ AJOUTER UN COMPLEMENT ═══");
        
        System.out.print("Nom du complément : ");
        String nom = scanner.nextLine();
        
        System.out.println("\nType :");
        System.out.println("1. Boisson");
        System.out.println("2. Frites");
        System.out.print("Votre choix : ");
        int typeChoix = lireChoix();
        
        TypeComplement type;
        if (typeChoix == 1) {
            type = TypeComplement.BOISSON;
        } else if (typeChoix == 2) {
            type = TypeComplement.FRITES;
        } else {
            System.out.println("❌ Type invalide");
            return;
        }
        
        System.out.print("Prix (FCFA) : ");
        BigDecimal prix = new BigDecimal(scanner.nextLine());
        
        System.out.print("Chemin de l'image (vide pour ignorer) : ");
        String imagePath = scanner.nextLine();
        
        Complement complement = new Complement(nom, type, prix);
        
        File imageFile = null;
        if (!imagePath.trim().isEmpty()) {
            imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.out.println("⚠️  Image non trouvée, création sans image");
                imageFile = null;
            }
        }
        
        Complement created = complementService.create(complement, imageFile);
        
        if (created != null) {
            System.out.println("\n✅ Complément ajouté avec succès !");
            System.out.println("   ID : " + created.getId());
            System.out.println("   Nom : " + created.getNom());
            System.out.println("   Type : " + created.getType());
            System.out.println("   Prix : " + created.getPrix() + " FCFA");
        } else {
            System.out.println("\n❌ Échec de l'ajout du complément");
        }
    }
    
    private void listerParType(TypeComplement type) {
        String typeNom = (type == TypeComplement.BOISSON) ? "BOISSONS" : "FRITES";
        System.out.println("\n═══ LISTE DES " + typeNom + " ═══");
        
        List<Complement> complements = complementService.findByType(type);
        
        if (complements == null || complements.isEmpty()) {
            System.out.println("\n⚠️  Aucun complément trouvé");
            return;
        }
        
        afficherTableau(complements);
    }
    
    private void listerTous() {
        System.out.println("\n═══ LISTE DE TOUS LES COMPLEMENTS ═══");
        
        System.out.println("1. Tous les compléments");
        System.out.println("2. Compléments actifs seulement");
        System.out.print("Votre choix : ");
        
        int choix = lireChoix();
        
        List<Complement> complements;
        if (choix == 2) {
            complements = complementService.findAvailable();
        } else {
            complements = complementService.findAll();
        }
        
        if (complements.isEmpty()) {
            System.out.println("\n⚠️  Aucun complément trouvé");
            return;
        }
        
        afficherTableau(complements);
    }
    
    private void afficherTableau(List<Complement> complements) {
        System.out.println("\n┌──────┬────────────────────────┬───────────┬───────────┬──────────┐");
        System.out.println("│  ID  │         Nom            │   Type    │   Prix    │  Statut  │");
        System.out.println("├──────┼────────────────────────┼───────────┼───────────┼──────────┤");
        
        for (Complement complement : complements) {
            String statut = complement.getArchive() ? "Archivé" : "Actif";
            System.out.printf("│ %-4d │ %-22s │ %-9s │ %9s │ %-8s │%n",
                complement.getId(),
                truncate(complement.getNom(), 22),
                complement.getType(),
                complement.getPrix(),
                statut
            );
        }
        
        System.out.println("└──────┴────────────────────────┴───────────┴───────────┴──────────┘");
        System.out.println("\nTotal : " + complements.size() + " complément(s)");
    }
    
    private void modifier() {
        System.out.println("\n═══ MODIFIER UN COMPLEMENT ═══");
        
        System.out.print("ID du complément à modifier : ");
        int id = lireChoix();
        
        Complement complement = complementService.findById(id);
        if (complement == null) {
            System.out.println("❌ Complément introuvable");
            return;
        }
        
        System.out.println("\nComplément actuel :");
        System.out.println("  Nom : " + complement.getNom());
        System.out.println("  Type : " + complement.getType());
        System.out.println("  Prix : " + complement.getPrix() + " FCFA");
        
        System.out.print("\nNouveau nom (vide pour garder) : ");
        String nom = scanner.nextLine();
        if (!nom.trim().isEmpty()) {
            complement.setNom(nom);
        }
        
        System.out.print("Nouveau prix (vide pour garder) : ");
        String prixStr = scanner.nextLine();
        if (!prixStr.trim().isEmpty()) {
            complement.setPrix(new BigDecimal(prixStr));
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
        
        boolean updated = complementService.update(complement, imageFile);
        
        if (updated) {
            System.out.println("\n✅ Complément modifié avec succès !");
        } else {
            System.out.println("\n❌ Échec de la modification");
        }
    }
    
    private void archiver() {
        System.out.println("\n═══ ARCHIVER UN COMPLEMENT ═══");
        
        System.out.print("ID du complément à archiver : ");
        int id = lireChoix();
        
        Complement complement = complementService.findById(id);
        if (complement == null) {
            System.out.println("❌ Complément introuvable");
            return;
        }
        
        System.out.println("\nComplément à archiver : " + complement.getNom());
        System.out.print("Confirmer l'archivage ? (O/N) : ");
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("O") || confirmation.equalsIgnoreCase("OUI")) {
            boolean archived = complementService.archive(id);
            
            if (archived) {
                System.out.println("\n✅ Complément archivé avec succès !");
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