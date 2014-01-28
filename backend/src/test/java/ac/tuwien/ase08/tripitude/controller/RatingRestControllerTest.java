package ac.tuwien.ase08.tripitude.controller;

import static org.junit.Assert.*;

import java.util.Locale;

import javassist.NotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ac.tuwien.ase08.tripitude.controller.api.RatingRestController;
import ac.tuwien.ase08.tripitude.entity.Comment;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.Rating;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.PrepareServiceTest;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRatingService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@ContextConfiguration("/abstract-session-test.xml")
public class RatingRestControllerTest extends PrepareServiceTest {

	@Autowired
	private RatingRestController ratingController;
	
	@Autowired
	private IRatingService ratingService;
	
	@Autowired
	private ICoordinateService coordinateService;
	
	@Autowired
	private IHotspotService hotspotService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IRoleService roleService;
	
	@Autowired
    private AuthenticationManager am;
	
	private Model model;
	private Locale locale;

	private Hotspot mapItem1, mapItem2;

	private User user;
	private User user2;
	private User user3;

	@Before
	@Transactional
	public void setUp() {
		model = new ExtendedModelMap();
		locale = new Locale("Austria");
		
		user = new User("Keyser Soze", "secret", "keyser@gmail.com");
		user.setRole(roleService.getRoleByRole("AUTHENTICATED"));
		user.setPoints(0L);
		userService.add(user);
		
		mapItem1 = new Hotspot("Restaurant", "Best Restaurant with lots of pizza");
		mapItem1.setCoordinate(new Coordinate(20.0, 30.0));
		mapItem1.setUser(user);
		hotspotService.add(mapItem1);

		mapItem2 = new Hotspot("Cinema", "This is a huge cinema");
		mapItem2.setCoordinate(new Coordinate(20.0, 50.0));
		mapItem2.setUser(user);
		hotspotService.add(mapItem2);
		
		Authentication auth = new UsernamePasswordAuthenticationToken("keyser@gmail.com", "secret");
		SecurityContextHolder.getContext().setAuthentication(am.authenticate(auth));
	}
	
	@Test
	@Transactional
	public void testaddRatingToMapItem() throws NotFoundException {
		Rating r = new Rating(1, mapItem1, user);
		
		ratingController.addRatingToMapItem(mapItem1.getId(), r, locale, model);
		assertEquals(r, ratingService.findByUserAndMapItem(user, mapItem1));
		
		// now update the rating
		Rating r2 = new Rating (-1, mapItem1, user);
		ratingController.addRatingToMapItem(mapItem1.getId(), r2, locale, model);
		System.out.println(r2);
		System.out.println(ratingService.findByUserAndMapItem(user, mapItem1));
		
		Rating found = ratingService.findByUserAndMapItem(user, mapItem1);
		boolean same = false;
		if(r2.getRating() == found.getRating() && r2.getMapItem().equals(found.getMapItem()) && r2.getUser().equals(found.getUser())) same = true;

		assertTrue(same);
	}
	
	@Test(expected=NotFoundException.class)
	@Transactional
	public void testaddRatingToMapItemThrowsNotFoundException() throws NotFoundException {
		Rating r = new Rating(1, mapItem1, user);
		
		ratingController.addRatingToMapItem(new Long(-13), r, locale, model);
		fail();
	}
	
	@Test
	@Transactional
	public void testgetRatingByMapItemAndUser() throws NotFoundException {
		Rating r = new Rating(1, mapItem1, user);
		
		ratingService.add(r);
		assertEquals(r, ratingController.getRatingByMapItemAndUser(mapItem1.getId(), user.getId(), locale, model));
	}
	
	@Test(expected=NotFoundException.class)
	@Transactional
	public void testgetRatingByMapItemAndUserThrowsUserNotFoundException() throws NotFoundException {
		Rating r = new Rating(1, mapItem1, user);
		
		ratingService.add(r);
		ratingController.getRatingByMapItemAndUser(mapItem1.getId(), new Long(-13), locale, model);
		fail();
	}
	
	@Test(expected=NotFoundException.class)
	@Transactional
	public void testgetRatingByMapItemAndUserThrowsMapItemNotFoundException() throws NotFoundException {
		Rating r = new Rating(1, mapItem1, user);
		
		ratingService.add(r);
		ratingController.getRatingByMapItemAndUser(new Long(-13), user.getId(), locale, model);
		fail();
	}
	
	@Test(expected=NotFoundException.class)
	@Transactional
	public void testgetRatingByMapItemAndUserThrowsRatingNotFoundException() throws NotFoundException {
		ratingController.getRatingByMapItemAndUser(mapItem1.getId(), user.getId(), locale, model);
		fail();
	}
	
	@Test
	@Transactional
	public void testgetRatingByMapItemAndCurrentUser() throws NotFoundException {
		Rating r = new Rating(1, mapItem1, user);
		
		ratingService.add(r);
		assertEquals(r, ratingController.getRatingByMapItemAndCurrentUser(mapItem1.getId(), locale, model));
	}
	
	@Test(expected=NotFoundException.class)
	@Transactional
	public void testgetRatingByMapItemAndCurrentUserThrowsRatingNotFoundException() throws NotFoundException {
		ratingController.getRatingByMapItemAndCurrentUser(mapItem1.getId(), locale, model);
		fail();
	}
	
	@Test(expected=NotFoundException.class)
	@Transactional
	public void testgetRatingByMapItemAndCurrentUserThrowsMapItemNotFoundException() throws NotFoundException {
		Rating r = new Rating(1, mapItem1, user);
		
		ratingService.add(r);
		ratingController.getRatingByMapItemAndCurrentUser(new Long(-13), locale, model);
		fail();
	}
}
