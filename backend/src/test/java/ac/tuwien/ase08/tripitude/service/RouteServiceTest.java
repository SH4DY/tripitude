package ac.tuwien.ase08.tripitude.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Route;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRouteService;

public class RouteServiceTest extends PrepareServiceTest {
	
	@Autowired
	private IRouteService routeService;
	
	@Autowired
	private ICoordinateService coordinateService;
	
	private Route route = null;
	private Coordinate coord = null;
	List<Route> routelist = null;
	
	@Before
	public void setUp() {
		route = new Route("Schoenbrunn","Rundgangdesc");
		coord = new Coordinate(15D, 25D);
		coordinateService.add(coord);
		
		route.setCoordinate(coord);
		
		routelist = Arrays.asList(
				new Route("A","desc"), 
				new Route("B","desc"), 
				new Route("C","desc"));
		
	}

	@Test
	@Transactional
	public void testAddRoute() {
		
		routeService.add(route);
    	assertNotNull(route.getId());
    	Route found = routeService.find(route.getId());
    	
    	assertEquals(route, found);
	}
	
	@Test
	@Transactional
	public void testFindRoute() {
				
		routeService.add(route);		
		Route found = routeService.find(route.getId());
		
		assertNotNull(found);
		
		assertEquals(route.getCoordinate(), found.getCoordinate());
	}
	
	@Test
	@Transactional
	public void testUpdateRoute() {
				
		routeService.add(route);		
		route.setDescription("newdesc");
		
		Route found = routeService.find(route.getId());
		
		assertEquals(found.getDescription(), route.getDescription());
	}
	
	@Test
	@Transactional
	public void testListRoute() {
		
		assertEquals(0, routeService.list().size());
		
		for (Route temp : routelist) {
			temp.setCoordinate(coord);
			routeService.add(temp);
		}
		
		List<Route> found = routeService.list();
		assertEquals(3, found.size());
		for (Route coord : found){
			assertTrue(routelist.contains(coord));
		}
	}

}
