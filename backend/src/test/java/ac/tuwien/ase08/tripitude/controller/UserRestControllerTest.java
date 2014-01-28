package ac.tuwien.ase08.tripitude.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;

import javax.security.sasl.AuthenticationException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ac.tuwien.ase08.tripitude.controller.api.UserRestController;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.EventCategory;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.MapItemCategory;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.restauth.RestAuthLoginObj;
import ac.tuwien.ase08.tripitude.restauth.RestAuthUtil;
import ac.tuwien.ase08.tripitude.service.PrepareServiceTest;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@ContextConfiguration("/abstract-session-test.xml")
public class UserRestControllerTest extends PrepareServiceTest {

	private Model model;
	private Locale locale;
	private BindingResult result;
	
	@Autowired
	private AuthenticationManager am;
	@Autowired
	private IUserService userService;
	@Autowired
	private IEventService eventService;
	@Autowired
	private IMapItemCategoryService categoryService;
	@Autowired
	private ICoordinateService coordinateService;
	@Autowired
	private IHotspotService hotspotService;
	@Autowired
	private IEventCategoryService eventCategoryService;
	@Autowired
	private UserRestController userRestController;
	@Autowired
	private IRoleService roleService;
	@Autowired
    private RestAuthUtil restAuthUtil;
	
	protected MockHttpSession session;
	protected MockHttpServletRequest request;
	protected MockHttpServletResponse response;
	
	private User user = null;
	private MapItemCategory category1 = null;
	private Coordinate coord1 = null;
	private Hotspot hotspot = null;
	private EventCategory eCategory = null;
	private Event e = null, e2 = null;
	
	//private static final Logger logger = LoggerFactory.getLogger(UserRestControllerTest.class);
	
	@Before
	@Transactional
	public void setUp() {
		model = new ExtendedModelMap();
		locale = new Locale("Austria");
		
		user = new User("Keyser Soze", "secret", "keyser@gmail.com");
		user.setRole(roleService.getRoleByRole("AUTHENTICATED")); 
		user.setPoints(new Long(50));
		userService.add(user);
		
		User testUser = null;
		if(userService.list().size() < 2){
			testUser = new User();
			testUser.setEmail("test@test.com");
			testUser.setName("Testuser");
			testUser.setPassword("password");
			testUser.setRole(roleService.getRoleByRole("AUTHENTICATED"));
			testUser.setPoints(new Long(100));
			userService.add(testUser);
		}
		
		category1 = new MapItemCategory("Restaurant");
		categoryService.add(category1);		
		List<MapItemCategory> catlist = new ArrayList<MapItemCategory>();
		catlist.add(category1);
		
		coord1 = new Coordinate(48.195473, 16.369905);
		
		hotspot = new Hotspot("Schoenbrunn", "desc", 5D);
		hotspot.setCategories(catlist);		
		hotspot.setCoordinate(coord1);
		
		hotspotService.add(hotspot);
				
		//result = new BeanPropertyBindingResult(hotspot, "Errors");
		eCategory = new EventCategory("Essen");
		eventCategoryService.add(eCategory);
		List<EventCategory> eCatList = new ArrayList<EventCategory>();
		eCatList.add(eCategory);
		
		e = new Event();
		e.setName("Testevent");
		e.setMapItem(hotspot);
		e.setUser(user);
		e.setDescription("Testevent");
		e.setTime(new Date());
		e.setCategories(eCatList);
		
		e2 = new Event();
		e2.setName("Testevent2");
		e2.setMapItem(hotspot);
		e2.setUser(user);
		e2.setDescription("Testevent2");
		e2.setTime(new Date());
		e2.setCategories(eCatList);
		
		List<User> attenders = new ArrayList<User>();
		attenders.add(testUser);
		
		e.setAttendingUsers(attenders);
		e2.setAttendingUsers(attenders);
		
		eventService.add(e);
		eventService.add(e2);
		
		result = new BeanPropertyBindingResult(user, "Errors");
		
		Authentication auth = new UsernamePasswordAuthenticationToken("keyser@gmail.com", "secret"); 
		SecurityContextHolder.getContext().setAuthentication(am.authenticate(auth));
	}
	
	@Test
	@Transactional
	public void testGetUser() {
		User found = null;
		try {
			found = userRestController.get(user.getId(), locale, model);
		} catch (NotFoundException e) {
			fail("Should have worked.");
		}

		assertEquals(found, user);
		assertTrue(found.getPassword() == null);
	}
	
	@Test
	@Transactional
	public void testGetUserNotFound() {
		try {
			userRestController.get(new Long(0), locale, model);
			fail("Should have thrown an exception.");
		} catch (NotFoundException e) {
			assertTrue(e.getMessage().equals("user not found"));
		}
	}
	
	@Test
	@Transactional
	public void testList() {
		List<User> userList = userRestController.list(locale, model);
		
		assertEquals(userList.size(), 2);
		assertEquals(userList.get(0), user);
	}
	
	@Test
	@Transactional
	public void testListHighscores() {
		User u;
		int i = 0;
		while(userService.list().size() < 5) {
			u = new User();
			u.setEmail("test@test.com"+i);
			u.setName("Testuser"+i);
			u.setPassword("password"+i);
			u.setRole(roleService.getRoleByRole("AUTHENTICATED"));
			u.setPoints(new Long(100+i*20));
			userService.add(u);
			i++;
		}
		
		List<User> userList = userRestController.listHighscoreUser(locale, model);
		
		boolean condition = true;
		for (int j = 0; j < userList.size()-1; j++) {
			if(userList.get(j).getPoints() < userList.get(j+1).getPoints()) condition = false;
		}
		assertTrue(condition);
	}
	
	@Test
	@Transactional
	public void testGetCurrentUser() {
		User found = null;
		try {
			found = userRestController.currentUser(locale, model);
		} catch (NotFoundException e) {
			fail("There should be a current user.");
		}
		
		assertEquals(found, user);
		assertTrue(found.getPassword() == null);
	}
	
	@Test
	@Transactional
	public void testGetCurrentUserFail() {
		eventService.remove(e);
		eventService.remove(e2);
		userService.remove(user);
		try {
			userRestController.currentUser(locale, model);
			fail("There should be no current user.");
		} catch (NotFoundException e) {
			assertEquals(e.getMessage(), "current user not found");
		}
	}
	
	@Test
	@Transactional 
	public void testAutoLogin() { 
		startSession();
		startRequest();
		String password = encodeStringSH1(user.getPassword()); 
		String nonce = ""; 
		try { 
			nonce = userRestController.getNonce(locale, model).getNonce(); 
		} catch (NullPointerException e) {} 
		String hashedPassNonce = encodeStringSH1(nonce + password);

		RestAuthLoginObj loginObj = new RestAuthLoginObj(); 
		loginObj.setEmail(user.getEmail()); 
		loginObj.setHashedPassNonce(hashedPassNonce); 
		try { 
			userRestController.login(loginObj, locale, model); 
		} catch (AuthenticationException e) {
			fail("Should have worked.");
		} 
		finally{
			endRequest();
			endSession();
		}
	}
	
	//requires a user in the database
	@Test
	@Transactional 
	public void testLogin() { 
		eventService.remove(e);
		eventService.remove(e2);
		userService.remove(user);
		startSession();
		startRequest();
		String password = encodeStringSH1("password"); 
		String nonce = ""; 
		try { 
			nonce = userRestController.getNonce(locale, model).getNonce(); 
		} catch (NullPointerException e) {} 
		String hashedPassNonce = encodeStringSH1(nonce + password);

		RestAuthLoginObj loginObj = new RestAuthLoginObj(); 
		loginObj.setEmail("test@test.com"); 
		loginObj.setHashedPassNonce(hashedPassNonce); 
		try { 
			userRestController.login(loginObj, locale, model); 
		} catch (AuthenticationException e) {
			fail("Should have worked.");
		} 
		finally{
			endRequest();
			endSession();
		}
	}
	
	@Test
	@Transactional
	public void testLoginFail_WrongPassword() {
		eventService.remove(e);
		eventService.remove(e2);
		userService.remove(user);
		startSession();
		startRequest();
		String password = encodeStringSH1("WRONGpassword!");
		String nonce = ""; 
		try { 
			nonce = userRestController.getNonce(locale, model).getNonce(); 
		} catch (NullPointerException e) {} 
		String hashedPassNonce = encodeStringSH1(nonce + password);

		RestAuthLoginObj loginObj = new RestAuthLoginObj(); 
		loginObj.setEmail(user.getEmail()); 
		loginObj.setHashedPassNonce(hashedPassNonce); 
		try { 
			userRestController.login(loginObj, locale, model); 
			fail("Should not be able to login with wrong password.");
		} catch (AuthenticationException e) {
			assertEquals(e.getMessage(), "login failed");
		} 
		finally{
			endRequest();
			endSession();
		}
	}
	
	@Test
	@Transactional
	public void testLoginFail_WrongEmail() {
		eventService.remove(e);
		eventService.remove(e2);
		userService.remove(user);
		startSession();
		startRequest();
		String password = encodeStringSH1(user.getPassword());
		String nonce = ""; 
		try { 
			nonce = userRestController.getNonce(locale, model).getNonce(); 
		} catch (NullPointerException e) {} 
		String hashedPassNonce = encodeStringSH1(nonce + password);

		RestAuthLoginObj loginObj = new RestAuthLoginObj(); 
		loginObj.setEmail("wrong@email.net"); 
		loginObj.setHashedPassNonce(hashedPassNonce); 
		try { 
			userRestController.login(loginObj, locale, model);
			fail("Should not be able to login with wrong email address.");
		} catch (AuthenticationException e) {
			assertEquals(e.getMessage(), "login failed");
		} 
		finally{
			endRequest();
			endSession();
		}
	}
	
	@Test
	@Transactional
	public void testLogout() {
		startSession();
		startRequest();
		response = new MockHttpServletResponse();
		userRestController.logout(request, response, locale, model);
		try{
			userService.getCurrentUser();
			fail("There should be no current user.");
		}
		catch (Exception e){
			//Show if the error cause was a NullPointerException
			assertTrue(e instanceof NullPointerException);
		}
		finally{
			endRequest();
			endSession();
		}
	}
	
	@Test
	@Transactional
	public void testLogout_noCurrentUser() {
		//The logout shouldn't throw an exception - in any case.
		userService.remove(user);
		startSession();
		startRequest();
		response = new MockHttpServletResponse();
		userRestController.logout(request, response, locale, model);
		try{
			userService.getCurrentUser();
			fail("There should be no current user.");
		}
		catch (Exception e){
			//Show if the error cause was a NullPointerException
			assertTrue(e instanceof NullPointerException);
		}
		finally{
			endRequest();
			endSession();
		}
	}
	
	@Test
	@Transactional
	public void testRegister() {
		User register = new User();
		register.setName("NewUser");
		register.setEmail("NewUser@new.com");
		register.setPassword("Test123");
		register.setPasswordConfirmation("Test123");
		
		User returnedUser = null;
		
		try {
			returnedUser = (User)userRestController.add(register, result, locale, model);
		} catch (FieldErrorException e) {
			fail("This should work.");
		}
		
		assertEquals(returnedUser.getName() + returnedUser.getEmail(), register.getName() + register.getEmail());
	}
	
	@Test
	@Transactional
	public void testRegisterFail_differentPasswords() {
		User register = new User();
		register.setName("NewUser");
		register.setEmail("NewUser@new.com");
		register.setPassword("Test123");
		register.setPasswordConfirmation("Test312");
		
		try {
			userRestController.add(register, result, locale, model);
			fail("This should not work.");
		} catch (Exception e) {
			assertTrue(e instanceof FieldErrorException);
		}
	}
	
	@Test
	@Transactional
	public void testRegisterFail_existingEmail() {
		User register = new User();
		register.setName("Keyser Soze");
		register.setEmail("keyser@gmail.com");
		register.setPassword("superpassword");
		register.setPasswordConfirmation("superpassword");
		
		try {
			userRestController.add(register, result, locale, model);
			fail("This should not work.");
		} catch (Exception e) {
			assertTrue(e instanceof FieldErrorException);
		}
	}
	
	@Test
	@Transactional
	public void testGetEvents(){
		List<Event> events = userRestController.getEvents(locale, model);
		
		assertFalse(events.isEmpty());
		assertTrue(events.contains(e));
	}
	
	//helper methods for session and encoding
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
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

	protected void endRequest() {
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).requestCompleted();
        RequestContextHolder.resetRequestAttributes();
        request = null;
    }
    
	public static String encodeStringSH1(String string) {
		ShaPasswordEncoder encoder = new ShaPasswordEncoder();
		return encoder.encodePassword(string, "qsease08"); 
	} 
}
