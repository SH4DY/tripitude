package ac.tuwien.ase08.tripitude.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import ac.tuwien.ase08.tripitude.controller.api.RouteRestController;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.Route;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.restauth.RestAuthUtil;
import ac.tuwien.ase08.tripitude.service.PrepareServiceTest;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRouteService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@ContextConfiguration("/abstract-session-test.xml")
public class RouteRestControllerTest extends PrepareServiceTest {

	private Model model;
	private Locale locale;
	private BindingResult result;

	@Autowired
	private AuthenticationManager am;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRouteService routeService;
	@Autowired
	private RouteRestController routeRestController;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private RestAuthUtil restAuthUtil;

	protected MockHttpSession session;
	protected MockHttpServletRequest request;
	protected MockHttpServletResponse response;

	private User user = null;
	private Route route = null;

	// private static final Logger logger =
	// LoggerFactory.getLogger(UserRestControllerTest.class);

	public static class MockSecurityContext implements SecurityContext {

		private static final long serialVersionUID = -1386535243513362694L;

		private Authentication authentication;

		public MockSecurityContext(Authentication authentication) {
			this.authentication = authentication;
		}

		@Override
		public Authentication getAuthentication() {
			return this.authentication;
		}

		@Override
		public void setAuthentication(Authentication authentication) {
			this.authentication = authentication;
		}
	}

	@Before
	@Transactional
	public void setUp() {
		model = new ExtendedModelMap();
		locale = new Locale("Austria");

		user = new User("Keyser Soze", "secret", "keyser@gmail.com");
		user.setRole(roleService.getRoleByRole("AUTHENTICATED"));
		userService.add(user);

		route = new Route();
		route.setTitle("Testroute");
		route.setUser(user);
		route.setCoordinate(new Coordinate(49.195473, 17.369905));

		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		coordinates.add(new Coordinate(49.295473, 17.369905));
		coordinates.add(new Coordinate(49.395473, 17.369905));
		coordinates.add(new Coordinate(49.495473, 17.369905));

		route.setCoordinates(coordinates);

		Hotspot hotspot = new Hotspot("Schoenbrunn", "desc", 5D);
		hotspot.setCoordinate(new Coordinate(49.595473, 17.369905));

		List<Hotspot> hotspots = new ArrayList<Hotspot>();
		hotspots.add(hotspot);

		route.setHotspots(hotspots);
		route.setDescription("Test route");

		result = new BeanPropertyBindingResult(route, "Errors");

		Authentication auth = new UsernamePasswordAuthenticationToken(
				"keyser@gmail.com", "secret");
		SecurityContextHolder.getContext().setAuthentication(
				am.authenticate(auth));

		//MockHttpSession session = new MockHttpSession();
//		session.setAttribute(
//				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
//				new MockSecurityContext(auth));
	}

	@Test
	@Transactional
	public void testSaveRoute() {
		Route createdRoute = null;

		try {
			createdRoute = (Route) routeRestController.create(route, result,
					locale, model);
		} catch (FieldErrorException e) {
			fail("Should work.");
		}

		assertEquals(createdRoute, route);
		assertEquals(createdRoute.getCoordinates(), route.getCoordinates());
	}

	@Test
	@Transactional
	public void testGetRoute() {
		routeService.add(route);
		Route returnedRoute = null;
		try {
			returnedRoute = routeRestController.get(route.getId(), locale,
					model);
		} catch (NotFoundException e) {
			fail("Should work");
		}

		assertEquals(route, returnedRoute);
		assertEquals(route.getCoordinates(), returnedRoute.getCoordinates());
	}

	@Test
	@Transactional
	public void testGetRouteFail_notFound() {
		routeService.add(route);
		try {
			routeRestController.get(route.getId() + 1, locale, model);
		} catch (NotFoundException e) {
			assertEquals(e.getMessage(), "route not found");
		}
	}
}
