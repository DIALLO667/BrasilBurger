package sn.brasilburger.repository.impl;

import sn.brasilburger.config.DatabaseConfig;
import sn.brasilburger.entity.Complement;
import sn.brasilburger.entity.enums.TypeComplement;
import sn.brasilburger.repository.ComplementRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplementRepositoryImpl implements ComplementRepository {
    
    private Connection connection;
    
    public ComplementRepositoryImpl() {
        this.connection = DatabaseConfig.getConnection();
    }
    
    @Override
    public Complement create(Complement complement) {
        String sql = "INSERT INTO Complement (nom, type, prix, image, archive) " +
                     "VALUES (?, ?::varchar, ?, ?, ?) RETURNING id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, complement.getNom());
            stmt.setString(2, complement.getType().getValue());
            stmt.setBigDecimal(3, complement.getPrix());
            stmt.setString(4, complement.getImage());
            stmt.setBoolean(5, complement.getArchive());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                complement.setId(rs.getInt("id"));
                System.out.println(" Complément créé avec l'ID : " + complement.getId());
                return complement;
            }
            
        } catch (SQLException e) {
            System.err.println(" Erreur création complément : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Complement> findAll() {
        List<Complement> complements = new ArrayList<>();
        String sql = "SELECT * FROM Complement ORDER BY type, nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Complement complement = mapResultSetToComplement(rs);
                complements.add(complement);
            }
            
            System.out.println("✅ " + complements.size() + " complément(s) récupéré(s)");
            
        } catch (SQLException e) {
            System.err.println(" Erreur récupération compléments : " + e.getMessage());
            e.printStackTrace();
        }
        
        return complements;
    }
    
    @Override
    public Complement findById(Integer id) {
        String sql = "SELECT * FROM Complement WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Complement complement = mapResultSetToComplement(rs);
                System.out.println(" Complément trouvé : " + complement.getNom());
                return complement;
            } else {
                System.out.println("  Aucun complément trouvé avec l'ID : " + id);
            }
            
        } catch (SQLException e) {
            System.err.println(" Erreur recherche complément : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean update(Complement complement) {
        String sql = "UPDATE Complement SET nom = ?, type = ?::varchar, prix = ?, " +
                     "image = ?, archive = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, complement.getNom());
            stmt.setString(2, complement.getType().getValue());
            stmt.setBigDecimal(3, complement.getPrix());
            stmt.setString(4, complement.getImage());
            stmt.setBoolean(5, complement.getArchive());
            stmt.setInt(6, complement.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Complément mis à jour : " + complement.getNom());
                return true;
            } else {
                System.out.println("  Aucun complément mis à jour");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println(" Erreur mise à jour complément : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean archive(Integer id) {
        String sql = "UPDATE Complement SET archive = true WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Complément archivé (ID: " + id + ")");
                return true;
            } else {
                System.out.println("  Aucun complément archivé");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println(" Erreur archivage complément : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Complement> findByArchiveStatus(boolean archive) {
        List<Complement> complements = new ArrayList<>();
        String sql = "SELECT * FROM Complement WHERE archive = ? ORDER BY type, nom";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, archive);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Complement complement = mapResultSetToComplement(rs);
                complements.add(complement);
            }
            
            String statut = archive ? "archivés" : "actifs";
            System.out.println("✅ " + complements.size() + " complément(s) " + statut + " récupéré(s)");
            
        } catch (SQLException e) {
            System.err.println(" Erreur filtrage compléments : " + e.getMessage());
            e.printStackTrace();
        }
        
        return complements;
    }
    
    @Override
    public List<Complement> findByType(TypeComplement type) {
        List<Complement> complements = new ArrayList<>();
        String sql = "SELECT * FROM Complement WHERE type = ?::varchar AND archive = false ORDER BY nom";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.getValue());
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Complement complement = mapResultSetToComplement(rs);
                complements.add(complement);
            }
            
            System.out.println("✅ " + complements.size() + " complément(s) de type " + 
                             type + " récupéré(s)");
            
        } catch (SQLException e) {
            System.err.println(" Erreur recherche par type : " + e.getMessage());
            e.printStackTrace();
        }
        
        return complements;
    }
    
    
    private Complement mapResultSetToComplement(ResultSet rs) throws SQLException {
        Complement complement = new Complement();
        complement.setId(rs.getInt("id"));
        complement.setNom(rs.getString("nom"));
        complement.setType(TypeComplement.fromString(rs.getString("type")));
        complement.setPrix(rs.getBigDecimal("prix"));
        complement.setImage(rs.getString("image"));
        complement.setArchive(rs.getBoolean("archive"));
        complement.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        return complement;
    }
}