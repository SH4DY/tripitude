package ac.tuwien.ase08.tripitude.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

import ac.tuwien.ase08.tripitude.controller.api.HotspotRestController;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.MapItemCategory;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml")
public class HotspotRestControllerTest {
	
	private Model model;
	private Locale locale;
	private BindingResult result;
	

	@Autowired
	private HotspotRestController hotspotController;
	@Autowired
	private IHotspotService hotspotService;
	@Autowired
	private IMapItemCategoryService categoryService;
	@Autowired
	private ICoordinateService coordinateService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
    private AuthenticationManager am;

	private MapItemCategory category1 = null, category2 = null;
	private Coordinate coord1 = null;
	private Hotspot hotspot = null;
	private User user = null;
	
	
	protected MockHttpSession session;
	protected MockHttpServletRequest request;
	protected MockHttpServletResponse response;
	
	@Before
	@Transactional
	public void setUp() {
		model = new ExtendedModelMap();
		locale = new Locale("Austria");
		
		user = new User("Keyser Soze", "secret", "keyser@gmail.com");
		user.setRole(roleService.getRoleByRole("AUTHENTICATED"));
		userService.add(user);
		
		category1 = new MapItemCategory("Restaurant");
		categoryService.add(category1);		
		List<MapItemCategory> catlist = new ArrayList<MapItemCategory>();
		catlist.add(category1);
		
		coord1 = new Coordinate(48.195473, 16.369905);
		//coordinateService.add(coord1);
		
		
		hotspot = new Hotspot("Schoenbrunn", "desc", 5D);
		hotspot.setCategories(catlist);		
		hotspot.setCoordinate(coord1);
		
		Authentication auth = new UsernamePasswordAuthenticationToken("keyser@gmail.com", "secret");
		SecurityContextHolder.getContext().setAuthentication(am.authenticate(auth));
		

		result = new BeanPropertyBindingResult(hotspot, "Errors");
	}
	
	@Test
	@Transactional
	public void testUpdate() {
		startSession();
		startRequest();
		response = new MockHttpServletResponse();
		
		Hotspot responseHotspot = null;
		
		hotspotService.add(hotspot);
		
		hotspot.setTitle("new title");
		hotspot.setDescription("new desc");
		
		try {
			responseHotspot = hotspotController.update(hotspot, result, locale, model);
		} catch (FieldErrorException e) {
			fail();
			e.printStackTrace();
		}
		finally{
			endRequest();
			endSession();
		}
		
		
		Hotspot found = hotspotService.find(responseHotspot.getId());
		
		assertEquals(hotspot.getTitle(), found.getTitle());
		assertEquals(hotspot.getDescription(), found.getDescription());
		
	}
	
	@Test
	@Transactional
	public void testCreate() throws FieldErrorException{
		startSession();
		startRequest();
		response = new MockHttpServletResponse();
		
		Hotspot responseHotspot = null;
		
		try{
			responseHotspot = (Hotspot) hotspotController.create(hotspot, result, locale, model);
			assertNotNull(responseHotspot);		
		}
		catch (Exception e){
			fail();
		}
		finally{
			endRequest();
			endSession();
		}
		
		Hotspot found = hotspotService.find(responseHotspot.getId());
		
		assertEquals(hotspot.getCategories(), found.getCategories());
		assertEquals(hotspot.getCoordinate(), coordinateService.find(found.getCoordinate().getId()));
		assertEquals(hotspot.getUser(), found.getUser());
	}
	
	@Test
	@Transactional
	public void testList() throws FieldErrorException{
		startSession();
		startRequest();
		response = new MockHttpServletResponse();
		
		List<MapItem> hotspotsExpected = new ArrayList<MapItem>();
		hotspotsExpected.add(hotspot);
		
		List<MapItem> responseList = null;
		
		try{
			responseList = hotspotController.list("", "", "", "", locale, model);
			assertNotNull(responseList);		
		}
		catch (Exception e){
			fail();
		}
		finally{
			endRequest();
			endSession();
		}
		//assertEquals(hotspotsExpected, responseList);
	}
	
	@Test
	@Transactional
	public void testGet() throws FieldErrorException{
		startSession();
		startRequest();
		response = new MockHttpServletResponse();
		Hotspot responseHotspot = null;
		
		try{
			responseHotspot = (Hotspot) hotspotController.create(hotspot, result, locale, model);
			assertNotNull(responseHotspot);		
			responseHotspot = hotspotController.get(responseHotspot.getId(),locale, model);
		}
		catch (Exception e){
			fail();
		}
		finally{
			endRequest();
			endSession();
		}
		Hotspot found = hotspotService.find(hotspot.getId());
		assertEquals(found, responseHotspot);
	}

	// helper methods for session and encoding
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
