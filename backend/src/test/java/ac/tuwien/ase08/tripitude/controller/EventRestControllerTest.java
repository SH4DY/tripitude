package ac.tuwien.ase08.tripitude.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import ac.tuwien.ase08.tripitude.controller.api.EventRestController;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.EventCategory;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.MapItemCategory;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.restauth.RestAuthUtil;
import ac.tuwien.ase08.tripitude.service.PrepareServiceTest;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@ContextConfiguration("/abstract-session-test.xml")
public class EventRestControllerTest extends PrepareServiceTest {

	private Model model;
	private Locale locale;
	private BindingResult result;
	
	@Autowired
	private AuthenticationManager am;
	@Autowired
	private IUserService userService;
	@Autowired
	private EventRestController eventRestController;
	@Autowired
	private IRoleService roleService;
	@Autowired
    private RestAuthUtil restAuthUtil;
	@Autowired
	private IEventService eventService;
	@Autowired
	private IEventCategoryService eventCategoryService;
	@Autowired
	private IHotspotService hotspotService;
	@Autowired
	private IMapItemCategoryService categoryService;
	
	protected MockHttpSession session;
	protected MockHttpServletRequest request;
	protected MockHttpServletResponse response;

	private User user = null;
	private Event event = null, existingEvent = null;
	
	private static SimpleDateFormat sdf;
	private static final Logger logger = LoggerFactory.getLogger(UserRestControllerTest.class);
	
	
	@Before
	@Transactional
	public void setUp() {
		model = new ExtendedModelMap();
		locale = new Locale("Austria");
		
		sdf = new SimpleDateFormat("dd.MM.yyyy'T'hh:mm");
		
		user = new User("Keyser Soze", "secret", "keyser@gmail.com");
		user.setRole(roleService.getRoleByRole("AUTHENTICATED")); 
		userService.add(user);
		
		MapItemCategory category = new MapItemCategory("Restaurant");
		categoryService.add(category);		
		List<MapItemCategory> catlist = new ArrayList<MapItemCategory>();
		catlist.add(category);
		
		Coordinate coord1 = new Coordinate(48.195473, 16.369905);
		Hotspot hotspot = new Hotspot("Schoenbrunn", "desc", 5D);
		hotspot.setCategories(catlist);		
		hotspot.setCoordinate(coord1);
		
		hotspotService.add(hotspot);
		
		Authentication auth = new UsernamePasswordAuthenticationToken("keyser@gmail.com", "secret"); 
		SecurityContextHolder.getContext().setAuthentication(am.authenticate(auth));
		
		EventCategory ec = new EventCategory("Event Category");
		eventCategoryService.add(ec);
		List<EventCategory> eventCatList = new ArrayList<EventCategory>();
		eventCatList.add(ec);
		
		event = new Event();
		event.setName("Testevent");
		event.setDescription("This is just a test event.");
		event.setMapItem(hotspot);
		event.setUser(user);
		event.setCategories(eventCatList);
		
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
		
		result = new BeanPropertyBindingResult(event, "Errors");
	}
	
	@Test
	@Transactional
	public void testCreateEvent() {
			
		event.setTime(new Date(System.currentTimeMillis()+100000));
		
		Event createdEvent = null;
		try {
			createdEvent = (Event)eventRestController.create(event, result, locale, model);
		} catch (FieldErrorException e) {
			System.out.println(e.getMessage());
			fail("Should be possible to add this event.");
		}
		
		assertEquals(event, createdEvent);
	}
	
	@Test
	@Transactional
	public void testCreateEventFail_dateBeforeToday() {
		try {
			event.setTime(sdf.parse("01.01.2000T13:00"));
		} catch (ParseException e) {
			fail("This should work.");
		}
		try {
			eventRestController.create(event, result, locale, model);
			fail("Should not be possible to add this event.");
		} catch (Exception e) {
			assertTrue(e instanceof FieldErrorException);
		}
	}
	
	@Test
	@Transactional
	public void testGetEvent() {
		Event returnedEvent = null;
		try {
			returnedEvent = eventRestController.get(existingEvent.getId(), locale, model);
		} catch (NotFoundException e) {
			fail("Should work.");
		}
		assertEquals(returnedEvent, existingEvent);
	}
	
	@Test
	@Transactional
	public void testGetEventFail() {
		try {
			eventRestController.get((existingEvent.getId())+1, locale, model);
			fail("There should be no event with that id.");
		} catch(NotFoundException e){
			assertEquals(e.getMessage(), "event not found");
		}
	}
	
	@Test
	@Transactional
	public void testAttendEvent() {
		try {
			assertTrue((Boolean)eventRestController.attend(existingEvent.getId(), locale, model));
		} catch (FieldErrorException e) {
			fail("Should work.");
		} catch (NotFoundException e) {
			fail("Should work.");
		}
	}
	
	@Test
	@Transactional
	public void testAttendEventFail_EventNotFound() {
		try {
			eventRestController.attend(existingEvent.getId()+1, locale, model);
			fail("Should not work.");
		} catch (FieldErrorException e) {
			fail("Wrong exception.");
		} catch (NotFoundException e) {
			assertEquals(e.getMessage(), "event not found");
		}
	}
	
	@Test
	@Transactional
	public void testAttendEventFail_UserAlreadyAttending() {
		List<User> attendingUsers = new ArrayList<User>();
		attendingUsers.add(user);
		existingEvent.setAttendingUsers(attendingUsers);
		try {
			eventRestController.attend(existingEvent.getId(), locale, model);
			fail("Should not work.");
		} catch (FieldErrorException e) {
			assertTrue(e.getMessage().contains("403 Forbidden"));
		} catch (NotFoundException e) {
			fail("Wrong exception.");
		}
	}
	
	@Test
	@Transactional
	public void testGetAttendingUsers() {
		List<User> found = new ArrayList<User>();
		
		try {
			eventRestController.attend(existingEvent.getId(), locale, model);
		} catch (FieldErrorException e) {
			fail();
		} catch (NotFoundException e) {
			fail();
		}
		
		try {
			found = eventRestController.getAttendingUsers(existingEvent.getId(), locale, model);
		} catch (NotFoundException e) {
			fail();
			e.printStackTrace();
		}
		
		assertTrue(!found.isEmpty());
		assertEquals(found.get(0), user);
	}
	
	@Test
	@Transactional
	public void testUnattend() {
		List<User> found = new ArrayList<User>();
		
		testGetAttendingUsers();
		
		try {
			eventRestController.unattend(existingEvent.getId(), locale, model);
		} catch (FieldErrorException e) {
			fail();
		} catch (NotFoundException e) {
			fail();
		}
		
		try {
			found = eventRestController.getAttendingUsers(existingEvent.getId(), locale, model);
		} catch (NotFoundException e) {
			fail();
			e.printStackTrace();
		}
		
		assertTrue(found.isEmpty());
	}
}
