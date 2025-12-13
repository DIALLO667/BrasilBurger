package sn.brasilburger.repository;

import sn.brasilburger.entity.Complement;
import sn.brasilburger.entity.enums.TypeComplement;
import java.util.List;

public interface ComplementRepository {
    
    Complement create(Complement complement);
    
    List<Complement> findAll();
    
    Complement findById(Integer id);
    
    boolean update(Complement complement);
    
    boolean archive(Integer id);
    
    List<Complement> findByArchiveStatus(boolean archive);
    
    List<Complement> findByType(TypeComplement type);
}