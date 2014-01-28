package ac.tuwien.ase08.tripitude.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.mvel2.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.IHotspotDAO;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.MapItemCategory;
import ac.tuwien.ase08.tripitude.entity.Route;
import ac.tuwien.ase08.tripitude.search.MapItemSearchCriteria;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("hotspotService")
public class HotspotService extends MapItemService implements IHotspotService {
    
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private IHotspotDAO hotspotDAO;
	
	public void add(Hotspot entity) {
		hotspotDAO.add(entity);		
	}

	public void update(Hotspot entity) {
		hotspotDAO.update(entity);		
	}

	public void remove(Hotspot entity) {
		hotspotDAO.remove(entity);		
	}

	public Hotspot find(Long key) {
		return hotspotDAO.find(key);
	}

	public List<Hotspot> list() {
		return hotspotDAO.list();
	}
	
    public List<Hotspot> findByIds(List<Long> ids) {
		
		Query query = sessionFactory.getCurrentSession().createQuery("SELECT m FROM MapItem as m "
																	+ "WHERE m.id IN (:ids)");
		query.setParameterList("ids", ids);
				
		List l = query.list();
		
		return (List<Hotspot>) l;
	}
	
	public List<Hotspot> findByMapItemCategoryIds(List<Long> categoryIds) {
		
		Query query = sessionFactory.getCurrentSession().createQuery("SELECT m FROM MapItem as m "
																	+ "LEFT JOIN m.categories as c WHERE c.id IN (:category_ids)");
		query.setParameterList("category_ids", categoryIds);
				
		List l = query.list();
		
		return (List<Hotspot>) l;
	}
	
    public List<Hotspot> findByTitleLike(String titlelike) {
		
		Query query = sessionFactory.getCurrentSession().createQuery("SELECT m FROM MapItem as m "
																	+ "WHERE m.title LIKE :title");
		query.setParameter("title", "%" + titlelike + "%");
				
		List l = query.list();
		
		return (List<Hotspot>) l;
	}
    
    public List<MapItem> findByVariousCriteria(MapItemSearchCriteria searchCriteria) {
    	
    	return super.findByVariousCriteria(searchCriteria);
    }
    
    public Hotspot findFullHotspot(Long key) {
		
    	Hotspot h = hotspotDAO.find(key);
		
		if (h != null) {
			Hibernate.initialize(h.getComments());
		}
		return h;
	}
}
