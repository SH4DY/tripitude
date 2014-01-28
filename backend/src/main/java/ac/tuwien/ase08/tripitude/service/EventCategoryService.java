package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.ICoordinateDAO;
import ac.tuwien.ase08.tripitude.dao.interfaces.IEventCategoryDAO;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.EventCategory;
import ac.tuwien.ase08.tripitude.entity.MapItemCategory;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventCategoryService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("eventCategoryService")
public class EventCategoryService implements IEventCategoryService {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private IEventCategoryDAO eventCategoryDAO;
	
	public void add(EventCategory entity) {
		eventCategoryDAO.add(entity);		
	}

	public void update(EventCategory entity) {
		eventCategoryDAO.update(entity);		
	}

	public void remove(EventCategory entity) {
		eventCategoryDAO.remove(entity);		
	}

	public EventCategory find(Long key) {
		return eventCategoryDAO.find(key);
	}

	public List<EventCategory> list() {
		return eventCategoryDAO.list();
	}
	
	@Override
	public EventCategory getEventCategoryByName(String name) {
		
		Query query = sessionFactory.getCurrentSession().createQuery("FROM EventCategory WHERE name= :name");
		query.setParameter("name", name);
				
		List l = query.list();
		
		if (l.isEmpty()) {
			return null;
		}
		return (EventCategory) l.get(0);
	}

}
