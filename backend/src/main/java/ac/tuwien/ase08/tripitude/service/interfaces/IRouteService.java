package ac.tuwien.ase08.tripitude.service.interfaces;

import ac.tuwien.ase08.tripitude.entity.Route;

public interface IRouteService extends IGenericService<Route, Long> {
	
	public Route findFullRoute(Long key);
}
