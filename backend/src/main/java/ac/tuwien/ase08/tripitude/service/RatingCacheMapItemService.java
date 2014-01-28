package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.IRatingCacheMapItemDAO;
import ac.tuwien.ase08.tripitude.dao.interfaces.IRatingDAO;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.RatingCacheMapItem;
import ac.tuwien.ase08.tripitude.service.interfaces.IRatingCacheMapItemService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("ratingCacheMapItemService")
public class RatingCacheMapItemService implements IRatingCacheMapItemService{
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private IRatingCacheMapItemDAO ratingCacheMapItemDAO;
	
	@Override
	public void add(RatingCacheMapItem entity) {
		ratingCacheMapItemDAO.add(entity);
	}

	@Override
	public void update(RatingCacheMapItem entity) {
		ratingCacheMapItemDAO.update(entity);		
	}

	@Override
	public void remove(RatingCacheMapItem entity) {
		ratingCacheMapItemDAO.remove(entity);
	}

	@Override
	public RatingCacheMapItem find(Long key) {
		return ratingCacheMapItemDAO.find(key);
	}

	@Override
	public List<RatingCacheMapItem> list() {
		return ratingCacheMapItemDAO.list();
	}
	
	public RatingCacheMapItem findByMapItem(MapItem mapItem) {
		
		Query query = sessionFactory.getCurrentSession().createQuery("FROM RatingCacheMapItem WHERE mapItem.id = :id");
		query.setParameter("id", mapItem.getId());
		
		List<RatingCacheMapItem> list = query.list();
		
		if (list.size() > 0) {
			
			return list.get(0);
		}
		
		return null;
	}

}
