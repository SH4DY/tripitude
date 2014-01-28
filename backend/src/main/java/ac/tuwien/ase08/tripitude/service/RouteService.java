package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.ICoordinateDAO;
import ac.tuwien.ase08.tripitude.dao.interfaces.IRouteDAO;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Route;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IRouteService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("routeService")
public class RouteService implements IRouteService {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private IRouteDAO routeDAO;
	
	public void add(Route entity) {
		routeDAO.add(entity);		
	}

	public void update(Route entity) {
		routeDAO.update(entity);		
	}

	public void remove(Route entity) {
		routeDAO.remove(entity);		
	}

	public Route find(Long key) {
		return routeDAO.find(key);
	}

	public List<Route> list() {
		return routeDAO.list();
	}
	
	public Route findFullRoute(Long key) {
		
		Route r = routeDAO.find(key);
		
		if (r != null) {
			
			Hibernate.initialize(r.getCoordinates());
			Hibernate.initialize(r.getHotspots());
			Hibernate.initialize(r.getComments());
		}
		return r;
	}

}
