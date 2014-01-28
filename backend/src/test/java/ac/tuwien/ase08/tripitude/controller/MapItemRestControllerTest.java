package ac.tuwien.ase08.tripitude.controller;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.validation.constraints.AssertFalse;

import javassist.NotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ac.tuwien.ase08.tripitude.controller.api.MapItemRestController;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.EventCategory;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.MapItemCategory;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.service.PrepareServiceTest;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;


public class MapItemRestControllerTest extends PrepareServiceTest {

	private Model model;
	private Locale locale;
	private BindingResult result;
	

	@Autowired
	private MapItemRestController mapItemController;
	@Autowired
	private IHotspotService hotspotService;
	@Autowired
	private IMapItemCategoryService categoryService;
	@Autowired
	private ICoordinateService coordinateService;
	@Autowired
	private IEventService eventService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IMapItemService mapItemService;
	@Autowired
	private IEventCategoryService eventCategoryService;
	@Autowired
	private IRoleService roleService;
	@Autowired
    private AuthenticationManager am;

	private MapItemCategory category1 = null, category2 = null;
	private Coordinate coord1 = null;
	private Hotspot hotspot = null;
	private User user = null;
	private Event existingEvent = null;
	private static SimpleDateFormat sdf;
	protected MockHttpSession session;
	protected MockHttpServletRequest request;
	protected MockHttpServletResponse response;
	
	@Before
	@Transactional
	public void setUp() {
		user = new User("Keyser Soze", "secret", "keyser@gmail.com");
		user.setRole(roleService.getRoleByRole("AUTHENTICATED"));
		userService.add(user);
		
		model = new ExtendedModelMap();
		locale = new Locale("Austria");
		
		sdf = new SimpleDateFormat("dd.MM.yyyy'T'hh:mm");
		
		category1 = new MapItemCategory("Restaurant");
		categoryService.add(category1);		
		List<MapItemCategory> catlist = new ArrayList<MapItemCategory>();
		catlist.add(category1);
		
		coord1 = new Coordinate(48.195473, 16.369905);
		//coordinateService.add(coord1);
		
		
		hotspot = new Hotspot("Schoenbrunn", "desc", 5D);
		hotspot.setCategories(catlist);		
		hotspot.setCoordinate(coord1);
		
		hotspotService.add(hotspot);
		
		EventCategory ec = new EventCategory("Event Category");
		eventCategoryService.add(ec);
		List<EventCategory> eventCatList = new ArrayList<EventCategory>();
		eventCatList.add(ec);
		
		existingEvent = new Event();
		existingEvent.setName("Funevent");
		existingEvent.setDescription("This is so much fun here!");
		
		try {
			existingEvent.setTime(sdf.parse("15.04.2014T09:00"));
		} catch (ParseException e) {
			//should not happen.
		}
		
		existingEvent.setMapItem(hotspot);
		existingEvent.setUser(user);
		existingEvent.setCategories(eventCatList);
		
		eventService.add(existingEvent);
		
		Authentication auth = new UsernamePasswordAuthenticationToken("keyser@gmail.com", "secret");
		SecurityContextHolder.getContext().setAuthentication(am.authenticate(auth));
		
				
		result = new BeanPropertyBindingResult(hotspot, "Errors");
	}
	

	
	@Test
	@Transactional
	public void testList() {
		String id = hotspot.getId().toString();
		String catid = category1.getId().toString(); 
		
		List<MapItem> itemlist = mapItemController.list(id, "hotspot", catid, "sch", "", "false", "", "", 200, false, "AND", locale, model);		
		
		assertEquals(itemlist.size(), 1);
	}
	
	@Test
	@Transactional
	public void testListWithEvents() throws ParseException {
		String id = hotspot.getId().toString();
		String catid = category1.getId().toString(); 
		Date beginDate = sdf.parse("15.04.2014T08:00");
		Date endDate = sdf.parse("15.04.2014T10:00");
		
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
		
		List<MapItem> itemlist = mapItemController.list(id, "hotspot", catid, "", "", "true", format.format(beginDate), format.format(endDate), 200, false, "AND", locale, model);		
		
		assertEquals(itemlist.size(), 1);
	}
	
	@Test
	@Transactional
	public void testGetByID() {
		long id = hotspot.getId();
		MapItem found = null;
		
		try {
			found = mapItemController.get(id, locale, model);
		} catch (NotFoundException e) {
			fail();
			e.printStackTrace();
		}
		
		assertEquals(found.getDescription(), hotspot.getDescription());
		assertEquals(found.getTitle(), hotspot.getTitle());
		assertEquals(found.getCategories(), hotspot.getCategories());
	}
	
	@Test(expected=NotFoundException.class)
	@Transactional
	public void testGetByIdException() throws NotFoundException {
		
		MapItem found = mapItemController.get(-1L, locale, model);
		
		fail();
	}
	
	@Test
	@Transactional
	public void testGetEventByMapItem() {
		long id = hotspot.getId();
		
		List<Event> found = null;
		
		try {
			found = mapItemController.getEvents(id, locale, model);
		} catch (NotFoundException e) {
			fail();
			e.printStackTrace();
		}
		
		assertEquals(found.get(0), existingEvent);
	}
	
	@Test(expected=NotFoundException.class)
	@Transactional
	public void testGetEventByMapItemException() throws NotFoundException {
		List<Event> found = mapItemController.getEvents(-1L, locale, model);
		
		fail();
	}
	
	@Test
	@Transactional
	public void testCreateHistoryItem() {
		Boolean result = false;
		Boolean result2 = true;
		startSession();
		startRequest();

		try {
			result = mapItemController.createHistoryItem(hotspot.getId(), locale, model);
			result2 = mapItemController.createHistoryItem(hotspot.getId(), locale, model);
		} catch (FieldErrorException e) {
			fail();
			e.printStackTrace();
		} finally {
			endRequest();
			endSession();
		}
		
		assertTrue(result);
		assertFalse(result2);
	}
	
	@Test(expected=FieldErrorException.class)
	@Transactional
	public void testCreateHistoryItemException() throws  FieldErrorException {
		Boolean result = false;
		startSession();
		startRequest();
		
		result = mapItemController.createHistoryItem(-1L,
				locale, model);
	}
	
	protected void startSession() {
		session = new MockHttpSession();
	}

	protected void endSession() {
		session.clearAttributes();
		session = null;
	}

	protected void startRequest() {
		request = new MockHttpServletRequest();
		request.setSession(session);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(
				request));
	}

	protected void endRequest() {
		((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.requestCompleted();
		RequestContextHolder.resetRequestAttributes();
		request = null;
	}
	
}
