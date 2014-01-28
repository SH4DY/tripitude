package ac.tuwien.ase08.tripitude.dao;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IRoleDAO;
import ac.tuwien.ase08.tripitude.entity.Role;


@Repository
public class RoleDAO extends HibernateDAO<Role, Long> implements IRoleDAO {

}
