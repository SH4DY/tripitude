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
import ac.tuwien.ase08.tripitude.entity.Rating;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRatingService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

public class RatingServiceTest extends PrepareServiceTest {
	
	@Autowired
	private IRatingService ratingService;
	
	@Autowired
	private ICoordinateService coordinateService;
	
	@Autowired
	private IHotspotService hotspotService;
	
	@Autowired
	private IUserService userService;
	
	private Rating rating = null;
	private Coordinate coord = null;
	private Hotspot hotspot = null;
	private User user = null;
	List<Rating> ratinglist = null;
//	
//	@Before
//	public void setUp() {
//
//		coord = new Coordinate(15D, 25D);
//		coordinateService.add(coord);
//		
//		hotspot = new Hotspot("hotspot1", "desc", 0D);
//		hotspot.setCoordinate(coord);
//		hotspotService.add(hotspot);
//		
//		user = new User("Keyser Soze", "secret", "keyser@gmail.com");
//		userService.add(user);
//				
//		rating = new Rating(true);
//		rating.setMapItem(hotspot);
//		rating.setUser(user);
//				
//		ratinglist = Arrays.asList(
//				new Rating(true), 
//				new Rating(true), 
//				new Rating(false));
//		
//	}
//
//	@Test
//	@Transactional
//	public void testAddRating() {
//		
//		ratingService.add(rating);
//    	assertNotNull(rating.getId());
//    	Rating found = ratingService.find(rating.getId());
//    	
//    	assertEquals(rating, found);
//	}
//	
//	@Test
//	@Transactional
//	public void testFindRating() {
//				
//		ratingService.add(rating);		
//		Rating found = ratingService.find(rating.getId());
//		
//		assertNotNull(found);
//
//    	assertEquals(found.getUser(), rating.getUser());
//    	assertEquals(found.getMapItem(), rating.getMapItem());
//	}
//	
//	@Test
//	@Transactional
//	public void testUpdateRating() {
//				
//		ratingService.add(rating);		
//		rating.setRate(false);
//		
//		Rating found = ratingService.find(rating.getId());
//		
//		assertEquals(found.getMapItem(), rating.getMapItem());
//	}
//	
//	@Test
//	@Transactional
//	public void testListRating() {
//		
//		assertEquals(0, ratingService.list().size());
//		
//		for (Rating temp : ratinglist) {
//			temp.setUser(user);
//			temp.setMapItem(hotspot);			
//			
//			ratingService.add(temp);
//		}
//		
//		List<Rating> found = ratingService.list();
//		assertEquals(3, found.size());
//		for (Rating coord : found){
//			assertTrue(ratinglist.contains(coord));
//		}
//	}

}
