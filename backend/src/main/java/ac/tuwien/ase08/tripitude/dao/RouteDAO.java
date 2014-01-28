package ac.tuwien.ase08.tripitude.dao;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IRouteDAO;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Route;

@Repository
public class RouteDAO extends HibernateDAO<Route, Long> implements IRouteDAO {
	
	public void add(Route route) {
		
		//be sure that all coordinates are initialized
		if (route.getCoordinate() != null) {			
			route.getCoordinate().setRadians();
		}
		
		for(Coordinate coordinate : route.getCoordinates()) {
			coordinate.setRadians();
		}
		
		super.add(route);        
    }
}
