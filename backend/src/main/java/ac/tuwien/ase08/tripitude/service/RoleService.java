package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.IRoleDAO;
import ac.tuwien.ase08.tripitude.entity.Role;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("roleService")
public class RoleService implements IRoleService{
    
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private IRoleDAO roleDAO;
	
	public void add(Role entity) {
		roleDAO.add(entity);		
	}

	public void update(Role entity) {
		roleDAO.update(entity);		
	}

	public void remove(Role entity) {
		roleDAO.remove(entity);		
	}

	public Role find(Long key) {
		return roleDAO.find(key);
	}

	public List<Role> list() {
		return roleDAO.list();
	}
	
	public Role getRoleByRole(String role) {
		Query query = sessionFactory.getCurrentSession().createQuery("FROM Role WHERE role= :role");
		query.setParameter("role", role);
				
		List l = query.list();
		
		if (l.isEmpty()) {
			return null;
		}
		return (Role) l.get(0);
	}
}
