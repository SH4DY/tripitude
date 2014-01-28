package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.IMapItemCategoryDAO;
import ac.tuwien.ase08.tripitude.entity.MapItemCategory;
import ac.tuwien.ase08.tripitude.entity.Role;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemCategoryService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("mapItemCategoryService")
public class MapItemCategoryService implements IMapItemCategoryService {
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private IMapItemCategoryDAO mapItemCategoryDAO;
	
	public void add(MapItemCategory entity) {
		mapItemCategoryDAO.add(entity);		
	}

	public void update(MapItemCategory entity) {
		mapItemCategoryDAO.update(entity);		
	}

	public void remove(MapItemCategory entity) {
		mapItemCategoryDAO.remove(entity);		
	}

	public MapItemCategory find(Long key) {
		return mapItemCategoryDAO.find(key);
	}

	public List<MapItemCategory> list() {
		return mapItemCategoryDAO.list();
	}

	@Override
	public MapItemCategory getMapItemCategoryByName(String name) {
		
		Query query = sessionFactory.getCurrentSession().createQuery("FROM MapItemCategory WHERE name= :name");
		query.setParameter("name", name);
				
		List l = query.list();
		
		if (l.isEmpty()) {
			return null;
		}
		return (MapItemCategory) l.get(0);
	}
}
