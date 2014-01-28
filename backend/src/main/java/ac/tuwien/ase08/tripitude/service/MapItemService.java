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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.tags.ParamAware;

import ac.tuwien.ase08.tripitude.controller.api.CommentRestController;
import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.File;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.search.EventSearchCriteria;
import ac.tuwien.ase08.tripitude.search.MapItemSearchCriteria;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemService;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@Service("mapItemService")
public class MapItemService implements IMapItemService {

	private static final Logger logger = LoggerFactory
			.getLogger(MapItemService.class);
	
	@Autowired
	private SessionFactory sessionFactory;

	public List<MapItem> findByVariousCriteria(
			MapItemSearchCriteria searchCriteria) {

		String queryString = "SELECT m FROM MapItem as m ";

		String joins = "";
		String orderBy = " ORDER BY rand()";
		
		List<String> conditions = new ArrayList<String>();
		Map<String, Object> parameterMap = new HashMap<String, Object>();

		// prepare joins and conditions
		if (searchCriteria.getIds().size() > 0) {
			conditions.add(" m.id IN (:ids) ");
			parameterMap.put("ids", searchCriteria.getIds());
		}
		if (searchCriteria.getTypes().size() > 0) {
			conditions.add(" m.class IN (:types) ");
			parameterMap.put("types", searchCriteria.getTypes());
		}
		if (searchCriteria.getCategoryIds().size() > 0) {
			joins += " LEFT JOIN m.categories AS c ";
			conditions.add(" c.id IN (:category_ids) ");
			parameterMap.put("category_ids", searchCriteria.getCategoryIds());
		}
		if (searchCriteria.getTitlelike().length() > 0) {
			conditions.add(" m.title LIKE :title ");
			parameterMap
					.put("title", "%" + searchCriteria.getTitlelike() + "%");
		}
		if (searchCriteria.getBoundingCircleCriteria() != null) {
			
			//prepare bounding circle criteria
			//find MapItems within a circle with given center and radius
			joins += " LEFT JOIN m.coordinate AS co ";
			conditions.add(" acos(sin(:lat_rad) * sin(co.latitudeRad) + cos(:lat_rad) * cos(co.latitudeRad) * "
					       + "cos(co.longitudeRad - (:long_rad))) * 6371000 <= :radius ");
			
			parameterMap.put("lat_rad", searchCriteria.getBoundingCircleCriteria().getCenter().getLatitudeRad());
			parameterMap.put("long_rad", searchCriteria.getBoundingCircleCriteria().getCenter().getLongitudeRad());
			parameterMap.put("radius", searchCriteria.getBoundingCircleCriteria().getRadius());
		}
		if (searchCriteria.getOrderByRating()) {
			//added join to be able to order by null values too
			joins += " LEFT JOIN m.ratingCache AS rc ";
			orderBy = " ORDER BY rc.average DESC";
		}

		// get operator
		String operator = " AND ";
		switch (searchCriteria.getOperator()) {
		case Operator.AND:
			operator = " AND ";
			break;
		case Operator.OR:
			operator = " OR ";
			break;
		default:
			break;
		}

		// prepare query string
		
		queryString += joins;
		
		if (conditions.size() > 0) {
			queryString += " WHERE "
					+ StringUtils.join(conditions, operator);
		}
		
		//add order criteria
		queryString += orderBy;

		Query query = sessionFactory.getCurrentSession().createQuery(
				queryString);

		for (Entry<String, Object> entry : parameterMap.entrySet()) {

			if (entry.getValue() instanceof List<?>) {
				query.setParameterList(entry.getKey(),
						(List<?>) entry.getValue());
			} else {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		
		//limit result
		query.setMaxResults(searchCriteria.getMaxResults());

		List l = query.list();

		return (List<MapItem>) l;

	}
	
	public List<MapItem> findEventsByVariousCriteria(
			EventSearchCriteria searchCriteria) {

		String queryString = "SELECT m FROM MapItem as m ";

		String joins = "";
		String orderBy = " ORDER BY rand()";
		
		List<String> conditions = new ArrayList<String>();
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		
		// add event joins
		joins += " INNER JOIN m.events AS e ";

		// prepare joins and conditions
		if (searchCriteria.getIds().size() > 0) {
			conditions.add(" m.id IN (:ids) ");
			parameterMap.put("ids", searchCriteria.getIds());
		}
		if (searchCriteria.getTypes().size() > 0) {
			conditions.add(" m.class IN (:types) ");
			parameterMap.put("types", searchCriteria.getTypes());
		}
		if (searchCriteria.getCategoryIds().size() > 0) {
			joins += " LEFT JOIN m.categories AS c ";
			conditions.add(" c.id IN (:category_ids) ");
			parameterMap.put("category_ids", searchCriteria.getCategoryIds());
		}
		if (searchCriteria.getTitlelike().length() > 0) {
			conditions.add(" m.title LIKE :title ");
			parameterMap
					.put("title", "%" + searchCriteria.getTitlelike() + "%");
		}
		if (searchCriteria.getBoundingCircleCriteria() != null) {
			
			//prepare bounding circle criteria
			//find MapItems within a circle with given center and radius
			joins += " LEFT JOIN m.coordinate AS co ";
			conditions.add(" acos(sin(:lat_rad) * sin(co.latitudeRad) + cos(:lat_rad) * cos(co.latitudeRad) * "
					       + "cos(co.longitudeRad - (:long_rad))) * 6371000 <= :radius ");
			
			parameterMap.put("lat_rad", searchCriteria.getBoundingCircleCriteria().getCenter().getLatitudeRad());
			parameterMap.put("long_rad", searchCriteria.getBoundingCircleCriteria().getCenter().getLongitudeRad());
			parameterMap.put("radius", searchCriteria.getBoundingCircleCriteria().getRadius());
		}
		if (searchCriteria.getOrderByRating()) {
			//added join to be able to order by null values too
			joins += " LEFT JOIN m.ratingCache AS rc ";
			orderBy = " ORDER BY rc.average DESC";
		}
		
		// set time search criteria
		if(searchCriteria.getBeginDate() != null && searchCriteria.getEndDate() != null) {
			conditions.add(" e.time >= :beginDate AND e.time <= :endDate ");
			parameterMap.put("beginDate", searchCriteria.getBeginDate());
			parameterMap.put("endDate", searchCriteria.getEndDate());
		}

		// get operator
		String operator = " AND ";
		switch (searchCriteria.getOperator()) {
		case Operator.AND:
			operator = " AND ";
			break;
		case Operator.OR:
			operator = " OR ";
			break;
		default:
			break;
		}

		// prepare query string
		
		queryString += joins;
		
		if (conditions.size() > 0) {
			queryString += " WHERE "
					+ StringUtils.join(conditions, operator);
		}
		
		//add order criteria
		queryString += orderBy;

		Query query = sessionFactory.getCurrentSession().createQuery(
				queryString);

		for (Entry<String, Object> entry : parameterMap.entrySet()) {

			if (entry.getValue() instanceof List<?>) {
				query.setParameterList(entry.getKey(),
						(List<?>) entry.getValue());
			} else {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		
		//limit result
		query.setMaxResults(searchCriteria.getMaxResults());

		List l = query.list();

		return (List<MapItem>) l;

	}

	public MapItem find(Long id) {
		return (MapItem) sessionFactory.getCurrentSession().get(MapItem.class, id);
	}
	
	public MapItem findFullMapItem(Long id) {
		
		MapItem m = find(id);
		
		//We have to fetch the comments in this session, else we get
		//an exception at the service level
		if (m != null) {
			Hibernate.initialize(m.getComments());
			Hibernate.initialize(m.getPictures());
		}

		return m;
	}

	@Override
	public List<Event> findEvents(Long id) {
		Query query = sessionFactory.getCurrentSession().createQuery("SELECT e FROM Event AS e "
				+ "WHERE mapitem_id = :id");
		query.setParameter("id", id);
		
		List l = query.list();
		
		return (List<Event>) l;
	}
	
	public List<File> getPicturesOfMapItem(MapItem mapitem){
//		Query query = sessionFactory.getCurrentSession().createQuery("SELECT * FROM File as e "
//				+ "WHERE e.mapitem_id=:id");
		Query query = sessionFactory.getCurrentSession().createQuery("SELECT m FROM File as m "
				+ "WHERE m.mapitem LIKE :id");
		query.setParameter("id", mapitem);
		
		List<File> l = query.list();
		
		return l;
	}
}
