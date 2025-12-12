package sn.brasilburger.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Burger {
    private Integer id;
    private String nom;
    private BigDecimal prix;
    private String image;
    private String description;
    private Boolean archive;
    private LocalDateTime dateCreation;
    
    public Burger() {
        this.archive = false;
        this.dateCreation = LocalDateTime.now();
    }
    
    public Burger(String nom, BigDecimal prix, String description) {
        this();
        this.nom = nom;
        this.prix = prix;
        this.description = description;
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public BigDecimal getPrix() {
        return prix;
    }
    
    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getArchive() {
        return archive;
    }
    
    public void setArchive(Boolean archive) {
        this.archive = archive;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    @Override
    public String toString() {
        return String.format("Burger[id=%d, nom='%s', prix=%s FCFA]", 
            id, nom, prix);
    }
}