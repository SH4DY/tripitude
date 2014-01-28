package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.ICoordinateDAO;
import ac.tuwien.ase08.tripitude.dao.interfaces.IRatingCacheMapItemDAO;
import ac.tuwien.ase08.tripitude.dao.interfaces.IRatingDAO;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.Rating;
import ac.tuwien.ase08.tripitude.entity.RatingCacheMapItem;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IRatingCacheMapItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRatingService;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@Service("ratingService")
public class RatingService implements IRatingService {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private IRatingDAO ratingDAO;

	@Autowired
	private IRatingCacheMapItemService ratingCacheMapItemService;

	public void add(Rating entity) {
   
		//update rating cache
		updateRatingCache(entity, null);
		ratingDAO.add(entity);
	}

	public void update(Rating entity) {
		
		//load existing rating
		Rating existingRating = find(entity.getId());
		
		//update rating cache
	    updateRatingCache(entity, existingRating);
	    
	    sessionFactory.getCurrentSession().evict(existingRating);
		ratingDAO.update(entity);
	}

	public void remove(Rating entity) {
		ratingDAO.remove(entity);
	}

	public Rating find(Long key) {
		return ratingDAO.find(key);
	}

	public List<Rating> list() {
		return ratingDAO.list();
	}
	
	public Rating findByUserAndMapItem(User user, MapItem mapItem) {
		
		Query query = sessionFactory.getCurrentSession().createQuery("FROM Rating WHERE user=:user AND mapItem=:mapItem");
		query.setParameter("user", user);
		query.setParameter("mapItem", mapItem);
				
		List l = query.list();
		
		if (l.isEmpty()) {
			return null;
		}
		
		return (Rating)l.get(0);
	}

	private void updateRatingCache(Rating entity, Rating existingRating) {

		// calculate result values and store in cash
		RatingCacheMapItem rcmi = ratingCacheMapItemService
				.findByMapItem(entity.getMapItem());
		// create cache item if not exists
		if (rcmi == null) {
			rcmi = new RatingCacheMapItem();
			rcmi.setMapItem(entity.getMapItem());
			rcmi.setNumRatings(0);
			rcmi.setSum(0.0);
			rcmi.setAverage(0.0);
			ratingCacheMapItemService.add(rcmi);
		}

		// remove existing rating from cache
		if (existingRating != null) {			
			rcmi.setNumRatings(rcmi.getNumRatings() - 1);
			rcmi.setSum(rcmi.getSum() - existingRating.getRating());
		}
		
		
		// calculate cache values
		rcmi.setNumRatings(rcmi.getNumRatings() + 1);
		rcmi.setSum(rcmi.getSum() + entity.getRating());
		rcmi.setAverage(rcmi.getSum() / rcmi.getNumRatings());

		ratingCacheMapItemService.update(rcmi);
	}
}
