package sn.brasilburger.repository.impl;

import sn.brasilburger.config.DatabaseConfig;
import sn.brasilburger.entity.Burger;
import sn.brasilburger.entity.Complement;
import sn.brasilburger.entity.Menu;
import sn.brasilburger.entity.enums.TypeComplement;
import sn.brasilburger.repository.MenuRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuRepositoryImpl implements MenuRepository {
    
    private Connection connection;
    
    public MenuRepositoryImpl() {
        this.connection = DatabaseConfig.getConnection();
    }
    
    @Override
    public Menu create(Menu menu) {
        BigDecimal prixCalcule = calculateMenuPrice(
            menu.getBurgerId(), 
            menu.getBoissonId(), 
            menu.getFritesId()
        );
        
        if (prixCalcule == null) {
            System.err.println(" Impossible de calculer le prix du menu");
            return null;
        }
        
        menu.setPrixCalcule(prixCalcule);
        
        String sql = "INSERT INTO Menu (nom, image, burger_id, boisson_id, frites_id, prix_calcule, archive) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, menu.getNom());
            stmt.setString(2, menu.getImage());
            stmt.setInt(3, menu.getBurgerId());
            stmt.setInt(4, menu.getBoissonId());
            stmt.setInt(5, menu.getFritesId());
            stmt.setBigDecimal(6, menu.getPrixCalcule());
            stmt.setBoolean(7, menu.getArchive());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                menu.setId(rs.getInt("id"));
                System.out.println("✅ Menu créé avec l'ID : " + menu.getId() + 
                                 " (Prix: " + prixCalcule + " FCFA)");
                return menu;
            }
            
        } catch (SQLException e) {
            System.err.println(" Erreur création menu : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Menu> findAll() {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT * FROM Menu ORDER BY date_creation DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Menu menu = mapResultSetToMenu(rs);
                menus.add(menu);
            }
            
            System.out.println("✅ " + menus.size() + " menu(s) récupéré(s)");
            
        } catch (SQLException e) {
            System.err.println(" Erreur récupération menus : " + e.getMessage());
            e.printStackTrace();
        }
        
        return menus;
    }
    
    @Override
    public Menu findById(Integer id) {
        String sql = "SELECT * FROM Menu WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Menu menu = mapResultSetToMenu(rs);
                System.out.println("✅ Menu trouvé : " + menu.getNom());
                return menu;
            } else {
                System.out.println("  Aucun menu trouvé avec l'ID : " + id);
            }
            
        } catch (SQLException e) {
            System.err.println(" Erreur recherche menu : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean update(Menu menu) {
        // Recalculer le prix si les composants ont changé
        BigDecimal nouveauPrix = calculateMenuPrice(
            menu.getBurgerId(), 
            menu.getBoissonId(), 
            menu.getFritesId()
        );
        
        if (nouveauPrix != null) {
            menu.setPrixCalcule(nouveauPrix);
        }
        
        String sql = "UPDATE Menu SET nom = ?, image = ?, burger_id = ?, boisson_id = ?, " +
                     "frites_id = ?, prix_calcule = ?, archive = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, menu.getNom());
            stmt.setString(2, menu.getImage());
            stmt.setInt(3, menu.getBurgerId());
            stmt.setInt(4, menu.getBoissonId());
            stmt.setInt(5, menu.getFritesId());
            stmt.setBigDecimal(6, menu.getPrixCalcule());
            stmt.setBoolean(7, menu.getArchive());
            stmt.setInt(8, menu.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Menu mis à jour : " + menu.getNom() + 
                                 " (Nouveau prix: " + menu.getPrixCalcule() + " FCFA)");
                return true;
            } else {
                System.out.println("  Aucun menu mis à jour");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println(" Erreur mise à jour menu : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean archive(Integer id) {
        String sql = "UPDATE Menu SET archive = true WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Menu archivé (ID: " + id + ")");
                return true;
            } else {
                System.out.println("  Aucun menu archivé");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println(" Erreur archivage menu : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Menu> findByArchiveStatus(boolean archive) {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT * FROM Menu WHERE archive = ? ORDER BY date_creation DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, archive);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Menu menu = mapResultSetToMenu(rs);
                menus.add(menu);
            }
            
            String statut = archive ? "archivés" : "actifs";
            System.out.println("✅ " + menus.size() + " menu(s) " + statut + " récupéré(s)");
            
        } catch (SQLException e) {
            System.err.println(" Erreur filtrage menus : " + e.getMessage());
            e.printStackTrace();
        }
        
        return menus;
    }
    
    @Override
    public Menu findByIdWithDetails(Integer id) {
        String sql = "SELECT " +
                     "m.id, m.nom, m.image, m.burger_id, m.boisson_id, m.frites_id, " +
                     "m.prix_calcule, m.archive, m.date_creation, " +
                     "b.id as b_id, b.nom as b_nom, b.prix as b_prix, b.image as b_image, " +
                     "b.description as b_description, b.archive as b_archive, " +
                     "bo.id as bo_id, bo.nom as bo_nom, bo.type as bo_type, bo.prix as bo_prix, " +
                     "bo.image as bo_image, bo.archive as bo_archive, " +
                     "f.id as f_id, f.nom as f_nom, f.type as f_type, f.prix as f_prix, " +
                     "f.image as f_image, f.archive as f_archive " +
                     "FROM Menu m " +
                     "JOIN Burger b ON m.burger_id = b.id " +
                     "JOIN Complement bo ON m.boisson_id = bo.id " +
                     "JOIN Complement f ON m.frites_id = f.id " +
                     "WHERE m.id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Menu menu = mapResultSetToMenuWithDetails(rs);
                System.out.println("✅ Menu avec détails trouvé : " + menu.getNom());
                return menu;
            } else {
                System.out.println("  Aucun menu trouvé avec l'ID : " + id);
            }
            
        } catch (SQLException e) {
            System.err.println(" Erreur recherche menu avec détails : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Menu> findAllWithDetails() {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT " +
                     "m.id, m.nom, m.image, m.burger_id, m.boisson_id, m.frites_id, " +
                     "m.prix_calcule, m.archive, m.date_creation, " +
                     "b.id as b_id, b.nom as b_nom, b.prix as b_prix, b.image as b_image, " +
                     "b.description as b_description, b.archive as b_archive, " +
                     "bo.id as bo_id, bo.nom as bo_nom, bo.type as bo_type, bo.prix as bo_prix, " +
                     "bo.image as bo_image, bo.archive as bo_archive, " +
                     "f.id as f_id, f.nom as f_nom, f.type as f_type, f.prix as f_prix, " +
                     "f.image as f_image, f.archive as f_archive " +
                     "FROM Menu m " +
                     "JOIN Burger b ON m.burger_id = b.id " +
                     "JOIN Complement bo ON m.boisson_id = bo.id " +
                     "JOIN Complement f ON m.frites_id = f.id " +
                     "WHERE m.archive = false " +
                     "ORDER BY m.date_creation DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Menu menu = mapResultSetToMenuWithDetails(rs);
                menus.add(menu);
            }
            
            System.out.println("✅ " + menus.size() + " menu(s) avec détails récupéré(s)");
            
        } catch (SQLException e) {
            System.err.println(" Erreur récupération menus avec détails : " + e.getMessage());
            e.printStackTrace();
        }
        
        return menus;
    }
    
   
    private BigDecimal calculateMenuPrice(Integer burgerId, Integer boissonId, Integer fritesId) {
        String sql = "SELECT " +
                     "(SELECT prix FROM Burger WHERE id = ?) + " +
                     "(SELECT prix FROM Complement WHERE id = ?) + " +
                     "(SELECT prix FROM Complement WHERE id = ?) AS prix_total";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, burgerId);
            stmt.setInt(2, boissonId);
            stmt.setInt(3, fritesId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("prix_total");
            }
            
        } catch (SQLException e) {
            System.err.println(" Erreur calcul prix menu : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    private Menu mapResultSetToMenu(ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        menu.setId(rs.getInt("id"));
        menu.setNom(rs.getString("nom"));
        menu.setImage(rs.getString("image"));
        menu.setBurgerId(rs.getInt("burger_id"));
        menu.setBoissonId(rs.getInt("boisson_id"));
        menu.setFritesId(rs.getInt("frites_id"));
        menu.setPrixCalcule(rs.getBigDecimal("prix_calcule"));
        menu.setArchive(rs.getBoolean("archive"));
        menu.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        return menu;
    }
    
    
    private Menu mapResultSetToMenuWithDetails(ResultSet rs) throws SQLException {
        
        Menu menu = mapResultSetToMenu(rs);
        
        
        Burger burger = new Burger();
        burger.setId(rs.getInt("b_id"));
        burger.setNom(rs.getString("b_nom"));
        burger.setPrix(rs.getBigDecimal("b_prix"));
        burger.setImage(rs.getString("b_image"));
        burger.setDescription(rs.getString("b_description"));
        burger.setArchive(rs.getBoolean("b_archive"));
        menu.setBurger(burger);
        
        
        Complement boisson = new Complement();
        boisson.setId(rs.getInt("bo_id"));
        boisson.setNom(rs.getString("bo_nom"));
        boisson.setType(TypeComplement.fromString(rs.getString("bo_type")));
        boisson.setPrix(rs.getBigDecimal("bo_prix"));
        boisson.setImage(rs.getString("bo_image"));
        boisson.setArchive(rs.getBoolean("bo_archive"));
        menu.setBoisson(boisson);
        
        
        Complement frites = new Complement();
        frites.setId(rs.getInt("f_id"));
        frites.setNom(rs.getString("f_nom"));
        frites.setType(TypeComplement.fromString(rs.getString("f_type")));
        frites.setPrix(rs.getBigDecimal("f_prix"));
        frites.setImage(rs.getString("f_image"));
        frites.setArchive(rs.getBoolean("f_archive"));
        menu.setFrites(frites);
        
        return menu;
    }
}