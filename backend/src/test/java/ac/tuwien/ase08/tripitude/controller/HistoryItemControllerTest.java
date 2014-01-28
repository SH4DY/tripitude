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

import ac.tuwien.ase08.tripitude.controller.api.HistoryItemRestController;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.HistoryItem;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.MapItemCategory;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.restauth.RestAuthLoginObj;
import ac.tuwien.ase08.tripitude.restauth.RestAuthUtil;
import ac.tuwien.ase08.tripitude.service.PrepareServiceTest;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHistoryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@ContextConfiguration("/abstract-session-test.xml")
public class HistoryItemControllerTest extends PrepareServiceTest {

	private Model model;
	private Locale locale;
	private BindingResult result;
	
	@Autowired
	private AuthenticationManager am;
	@Autowired
	private IUserService userService;
	@Autowired
	private IHistoryItemService historyItemService;
	@Autowired
	private IMapItemCategoryService categoryService;
	@Autowired
	private ICoordinateService coordinateService;
	@Autowired
	private IHotspotService hotspotService;
	@Autowired
	private HistoryItemRestController historyItemRestController;
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
	private HistoryItem hItem = null;
	
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
		hotspot = new Hotspot("Schoenbrunn", "desc", 5D);
		hotspot.setCategories(catlist);		
		hotspot.setCoordinate(coord1);
		hotspotService.add(hotspot);
				
		hItem = new HistoryItem(new Date(), hotspot, user);
		historyItemService.add(hItem);
		
		result = new BeanPropertyBindingResult(user, "Errors");
		
		Authentication auth = new UsernamePasswordAuthenticationToken("keyser@gmail.com", "secret"); 
		SecurityContextHolder.getContext().setAuthentication(am.authenticate(auth));
	}
	
	@Test
	@Transactional
	public void testGetHistoryByUser() {
		List<HistoryItem> items = historyItemRestController.getHistoryByUser(user.getId(), locale, model);
		assertTrue(items.contains(hItem));
		assertFalse(items.isEmpty());
	}
	
	@Test
	@Transactional
	public void testGetEmptyHistoryByUser(){
		historyItemService.remove(hItem);
		List<HistoryItem> items = historyItemRestController.getHistoryByUser(user.getId(), locale, model);
		assertTrue(items.isEmpty());
	}
	
	@Test
	@Transactional
	public void testGetHistoryByCurrentUser() {
		List<HistoryItem> items = historyItemRestController.getHistoryByCurrentUser(locale, model);
		assertTrue(items.contains(hItem));
		assertFalse(items.isEmpty());
	}
	
	@Test
	@Transactional
	public void testGetHistoryByCurrentUser_noCurrentUser() {
		historyItemService.remove(hItem);
		userService.remove(user);
		List<HistoryItem> items = historyItemRestController.getHistoryByCurrentUser(locale, model);
		assertTrue(items.isEmpty());
	}
	
	@Test
	@Transactional
	public void testGetHistoryItem(){
		HistoryItem h = null;
		try {
			h = historyItemRestController.getHistoryItem(hItem.getId(), locale, model);
		} catch (NotFoundException e) {
			fail("Should have worked.");
		}
		assertTrue(h.equals(hItem));
	}
	
	@Test(expected=NotFoundException.class)
	@Transactional
	public void testGetHistoryItem_fail() throws NotFoundException{
		HistoryItem h = historyItemRestController.getHistoryItem(0L, locale, model);
	}
	
}
