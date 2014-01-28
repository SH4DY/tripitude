package ac.tuwien.ase08.tripitude.controller.api;

import java.util.Date;
import java.util.Locale;

import javassist.NotFoundException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ac.tuwien.ase08.tripitude.entity.HistoryItem;
import ac.tuwien.ase08.tripitude.entity.Route;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.service.interfaces.IHistoryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRouteService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@Controller
@RequestMapping("api")
public class RouteRestController {
	
	@Autowired
	IRouteService routeService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IHistoryItemService historyItemService;
	
	private static final Logger logger = LoggerFactory.getLogger(HotspotRestController.class);
	
	private static final Long CREATED_SOMETHING = 25L;
	
	@RequestMapping(value = "route/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Route get(@PathVariable Long id, Locale locale, Model model) throws NotFoundException {
		Route r = routeService.findFullRoute(id);
		
		if (r == null) {
			throw new NotFoundException("route not found");
		}
		
		return r;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "route", method = RequestMethod.POST)
	@ResponseBody
	public Object create(@Valid @RequestBody Route route, BindingResult result, Locale locale, Model model) throws FieldErrorException {
		User user = userService.getCurrentUser();
		route.setUser(user);
		logger.info("adding Route: " + route);
		routeService.add(route);		
		userService.addPoints(user, CREATED_SOMETHING);
		HistoryItem item = new HistoryItem(new Date(), route, user);
		historyItemService.add(item);
		return route;
	}
}
