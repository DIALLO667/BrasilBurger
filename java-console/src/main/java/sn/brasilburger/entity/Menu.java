package sn.brasilburger.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Menu {
    private Integer id;
    private String nom;
    private String image;
    private Integer burgerId;
    private Integer boissonId;
    private Integer fritesId;
    private BigDecimal prixCalcule;
    private Boolean archive;
    private LocalDateTime dateCreation;
    
    // Pour affichage (non stocké en BD)
    private Burger burger;
    private Complement boisson;
    private Complement frites;
    
    public Menu() {
        this.archive = false;
        this.dateCreation = LocalDateTime.now();
    }
    
    public Menu(String nom, Integer burgerId, Integer boissonId, Integer fritesId) {
        this();
        this.nom = nom;
        this.burgerId = burgerId;
        this.boissonId = boissonId;
        this.fritesId = fritesId;
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
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public Integer getBurgerId() {
        return burgerId;
    }
    
    public void setBurgerId(Integer burgerId) {
        this.burgerId = burgerId;
    }
    
    public Integer getBoissonId() {
        return boissonId;
    }
    
    public void setBoissonId(Integer boissonId) {
        this.boissonId = boissonId;
    }
    
    public Integer getFritesId() {
        return fritesId;
    }
    
    public void setFritesId(Integer fritesId) {
        this.fritesId = fritesId;
    }
    
    public BigDecimal getPrixCalcule() {
        return prixCalcule;
    }
    
    public void setPrixCalcule(BigDecimal prixCalcule) {
        this.prixCalcule = prixCalcule;
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
    
    public Burger getBurger() {
        return burger;
    }
    
    public void setBurger(Burger burger) {
        this.burger = burger;
    }
    
    public Complement getBoisson() {
        return boisson;
    }
    
    public void setBoisson(Complement boisson) {
        this.boisson = boisson;
    }
    
    public Complement getFrites() {
        return frites;
    }
    
    public void setFrites(Complement frites) {
        this.frites = frites;
    }
    
    @Override
    public String toString() {
        return String.format("Menu[id=%d, nom='%s', prix=%s FCFA]", 
            id, nom, prixCalcule);
    }
}