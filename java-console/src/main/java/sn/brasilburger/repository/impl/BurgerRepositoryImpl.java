package sn.brasilburger.repository.impl;

import sn.brasilburger.config.DatabaseConfig;
import sn.brasilburger.entity.Burger;
import sn.brasilburger.repository.BurgerRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BurgerRepositoryImpl implements BurgerRepository {
    
    private Connection connection;
    
    public BurgerRepositoryImpl() {
        this.connection = DatabaseConfig.getConnection();
    }
    
    @Override
    public Burger create(Burger burger) {
        String sql = "INSERT INTO Burger (nom, prix, image, description, archive) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, burger.getNom());
            stmt.setBigDecimal(2, burger.getPrix());
            stmt.setString(3, burger.getImage());
            stmt.setString(4, burger.getDescription());
            stmt.setBoolean(5, burger.getArchive());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                burger.setId(rs.getInt("id"));
                System.out.println("Burger créé avec l'ID : " + burger.getId());
                return burger;
            }
            
        } catch (SQLException e) {
            System.err.println(" Erreur création burger : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Burger> findAll() {
        List<Burger> burgers = new ArrayList<>();
        String sql = "SELECT * FROM Burger ORDER BY date_creation DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Burger burger = mapResultSetToBurger(rs);
                burgers.add(burger);
            }
            
            System.out.println("✅ " + burgers.size() + " burger(s) récupéré(s)");
            
        } catch (SQLException e) {
            System.err.println(" Erreur récupération burgers : " + e.getMessage());
            e.printStackTrace();
        }
        
        return burgers;
    }
    
    @Override
    public Burger findById(Integer id) {
        String sql = "SELECT * FROM Burger WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Burger burger = mapResultSetToBurger(rs);
                System.out.println(" Burger trouvé : " + burger.getNom());
                return burger;
            } else {
                System.out.println(" Aucun burger trouvé avec l'ID : " + id);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche burger : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean update(Burger burger) {
        String sql = "UPDATE Burger SET nom = ?, prix = ?, image = ?, " +
                     "description = ?, archive = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, burger.getNom());
            stmt.setBigDecimal(2, burger.getPrix());
            stmt.setString(3, burger.getImage());
            stmt.setString(4, burger.getDescription());
            stmt.setBoolean(5, burger.getArchive());
            stmt.setInt(6, burger.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Burger mis à jour : " + burger.getNom());
                return true;
            } else {
                System.out.println("  Aucun burger mis à jour");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur mise à jour burger : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean archive(Integer id) {
        String sql = "UPDATE Burger SET archive = true WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Burger archivé (ID: " + id + ")");
                return true;
            } else {
                System.out.println("  Aucun burger archivé");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur archivage burger : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Burger> findByArchiveStatus(boolean archive) {
        List<Burger> burgers = new ArrayList<>();
        String sql = "SELECT * FROM Burger WHERE archive = ? ORDER BY date_creation DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, archive);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Burger burger = mapResultSetToBurger(rs);
                burgers.add(burger);
            }
            
            String statut = archive ? "archivés" : "actifs";
            System.out.println("✅ " + burgers.size() + " burger(s) " + statut + " récupéré(s)");
            
        } catch (SQLException e) {
            System.err.println("Erreur filtrage burgers : " + e.getMessage());
            e.printStackTrace();
        }
        
        return burgers;
    }
    
    @Override
    public List<Burger> findByPriceRange(BigDecimal minPrix, BigDecimal maxPrix) {
        List<Burger> burgers = new ArrayList<>();
        String sql = "SELECT * FROM Burger WHERE prix BETWEEN ? AND ? " +
                     "AND archive = false ORDER BY prix ASC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, minPrix);
            stmt.setBigDecimal(2, maxPrix);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Burger burger = mapResultSetToBurger(rs);
                burgers.add(burger);
            }
            
            System.out.println("✅ " + burgers.size() + " burger(s) entre " + 
                             minPrix + " et " + maxPrix + " FCFA");
            
        } catch (SQLException e) {
            System.err.println(" Erreur recherche par prix : " + e.getMessage());
            e.printStackTrace();
        }
        
        return burgers;
    }
    
    /**
     * Méthode utilitaire pour mapper un ResultSet vers un objet Burger
     */
    private Burger mapResultSetToBurger(ResultSet rs) throws SQLException {
        Burger burger = new Burger();
        burger.setId(rs.getInt("id"));
        burger.setNom(rs.getString("nom"));
        burger.setPrix(rs.getBigDecimal("prix"));
        burger.setImage(rs.getString("image"));
        burger.setDescription(rs.getString("description"));
        burger.setArchive(rs.getBoolean("archive"));
        burger.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        return burger;
    }
}