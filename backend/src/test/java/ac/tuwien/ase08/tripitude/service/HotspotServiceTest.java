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
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;

public class HotspotServiceTest extends PrepareServiceTest {
	
	@Autowired
	private IHotspotService hotspotService;
	
	@Autowired
	private ICoordinateService coordinateService;
	
	private Hotspot hotspot = null;
	private Coordinate coord = null;
	List<Hotspot> hotspotlist = null;
	
	@Before
	public void setUp() {
		hotspot = new Hotspot("Stephansdom", "Beischreibung" , 0D);
		coord = new Coordinate(15D, 25D);
		coordinateService.add(coord);
		
		hotspot.setCoordinate(coord);
		
		hotspotlist = Arrays.asList(
				new Hotspot("A","desc",10D), 
				new Hotspot("B","desc",20D), 
				new Hotspot("C","desc",30D));
		
	}

	@Test
	@Transactional
	public void testAddHotspot() {
		
		hotspotService.add(hotspot);
    	assertNotNull(hotspot.getId());
    	Hotspot found = hotspotService.find(hotspot.getId());
    	
    	assertEquals(hotspot, found);
	}
	
	@Test
	@Transactional
	public void testFindHotspot() {
				
		hotspotService.add(hotspot);		
		Hotspot found = hotspotService.find(hotspot.getId());
		
		assertNotNull(found);
		
		assertEquals(hotspot.getCoordinate(), found.getCoordinate());
	}
	
	@Test
	@Transactional
	public void testUpdateHotspot() {
				
		hotspotService.add(hotspot);		
		hotspot.setCost(49.99);;
		
		Hotspot found = hotspotService.find(hotspot.getId());
		
		assertEquals(found.getCost(), hotspot.getCost());
	}
	
	@Test
	@Transactional
	public void testListHotspot() {
		
		assertEquals(0, hotspotService.list().size());
		
		for (Hotspot temp : hotspotlist) {
			temp.setCoordinate(coord);
			hotspotService.add(temp);
		}
		
		List<Hotspot> found = hotspotService.list();
		assertEquals(3, found.size());
		for (Hotspot coord : found){
			assertTrue(hotspotlist.contains(coord));
		}
	}
	
}
