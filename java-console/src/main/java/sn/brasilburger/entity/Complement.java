package sn.brasilburger.entity;

import sn.brasilburger.entity.enums.TypeComplement;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Complement {
    private Integer id;
    private String nom;
    private TypeComplement type;
    private BigDecimal prix;
    private String image;
    private Boolean archive;
    private LocalDateTime dateCreation;
    
    public Complement() {
        this.archive = false;
        this.dateCreation = LocalDateTime.now();
    }
    
    public Complement(String nom, TypeComplement type, BigDecimal prix) {
        this();
        this.nom = nom;
        this.type = type;
        this.prix = prix;
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
    
    public TypeComplement getType() {
        return type;
    }
    
    public void setType(TypeComplement type) {
        this.type = type;
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
        return String.format("Complement[id=%d, nom='%s', type=%s, prix=%s FCFA]", 
            id, nom, type, prix);
    }
}