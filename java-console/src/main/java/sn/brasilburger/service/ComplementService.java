package sn.brasilburger.service;

import sn.brasilburger.entity.Complement;
import sn.brasilburger.entity.enums.TypeComplement;
import java.io.File;
import java.util.List;

public interface ComplementService {
    
    Complement create(Complement complement, File imageFile);
    
    List<Complement> findAll();
    
    List<Complement> findAvailable();
    
    Complement findById(Integer id);
    
    boolean update(Complement complement, File newImageFile);
    
    boolean archive(Integer id);
    
    List<Complement> findByType(TypeComplement type);
    
    boolean validateComplement(Complement complement);
}