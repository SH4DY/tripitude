package ac.tuwien.ase08.tripitude.dao;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IFileDAO;
import ac.tuwien.ase08.tripitude.entity.File;

@Repository
public class FileDAO extends HibernateDAO<File, Long> implements IFileDAO {

}
