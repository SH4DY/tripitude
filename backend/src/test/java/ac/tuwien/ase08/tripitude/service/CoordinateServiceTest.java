package ac.tuwien.ase08.tripitude.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;

public class CoordinateServiceTest extends PrepareServiceTest {

	@Autowired
	private ICoordinateService coordinateService;
	
	private Coordinate coord1 = null;
	List<Coordinate> coordlist = null;
	
	@Before
	public void setUp() {
		coord1 = new Coordinate(10.5, 20.5);
		
		coordlist = Arrays.asList(
				new Coordinate(10D,5D),
				new Coordinate(20D,10D),
				new Coordinate(30D,15D));
	}
	
	@Test
	@Transactional
	public void testAddCoordinate() {
		    	
    	coordinateService.add(coord1);
    	assertNotNull(coord1.getId());
    	Coordinate coord2 = coordinateService.find(coord1.getId());
    	
    	assertEquals(coord1, coord2);
	}
	
	@Test
	@Transactional
	public void testFindCoordinate() {
				
		coordinateService.add(coord1);		
		Coordinate coord2 = coordinateService.find(coord1.getId());
		
		assertNotNull(coord2);
		
		assertEquals(coord1.getLatitude(), coord2.getLatitude());
	}
	
	@Test
	@Transactional
	public void testUpdateCoordinate() {
				
		coordinateService.add(coord1);		
		coord1.setLatitude(60D);
		
		Coordinate found = coordinateService.find(coord1.getId());
		
		assertEquals(found.getLatitude(), coord1.getLatitude());
	}
	
	@Test
	@Transactional
	public void testListCoordinate() {
		
		assertEquals(0, coordinateService.list().size());
		
		for (Coordinate coord : coordlist) {
			coordinateService.add(coord);
		}
		
		List<Coordinate> found = coordinateService.list();
		assertEquals(3, found.size());
		for (Coordinate coord : found){
			assertTrue(coordlist.contains(coord));
		}
	}
}
