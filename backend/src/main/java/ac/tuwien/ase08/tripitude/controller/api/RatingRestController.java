package ac.tuwien.ase08.tripitude.controller.api;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.Rating;
import ac.tuwien.ase08.tripitude.entity.RatingCacheMapItem;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRatingCacheMapItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRatingService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRouteService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;
import ac.tuwien.ase08.tripitude.validator.DefaultValidator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("api")
public class RatingRestController {
	
	@Autowired 
	private MessageSource messageSource;
	
	@Autowired
	IRatingService ratingService;
	
	@Autowired
	IUserService userService;
	
	@Autowired
	IMapItemService mapItemService;
	
	@Autowired
	IRouteService routeService;
	
	@Autowired
	IHotspotService hotspotService;
	
	@Autowired
	IRatingCacheMapItemService ratingCacheMapItemService;
	
	@Autowired
	private DefaultValidator defaultValidator;
	
	private static final Long RATED_SOMETHING = 2L;
	private static final Long GOT_SOMETHING_RATED = 10L;
	
	private static final Logger logger = LoggerFactory.getLogger(RatingRestController.class);
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "mapitem/{id}/rating", method = RequestMethod.POST)
	@ResponseBody
	public RatingCacheMapItem addRatingToMapItem(@PathVariable Long id, @RequestBody Rating rating, Locale locale, Model model) throws NotFoundException {
		
		User currentUser = userService.getCurrentUser();
		rating.setUser(currentUser);
		
		MapItem mapItem = mapItemService.find(id);
		
		if (mapItem == null) {
			throw new NotFoundException("mapitem not found");
		}
		
		Rating existingRating = ratingService.findByUserAndMapItem(currentUser, mapItem);
		
		if (existingRating != null) {
			
			existingRating.setRating(rating.getRating());
			ratingService.update(existingRating);
		}
		else {			
			rating.setMapItem(mapItem);
			ratingService.add(rating);
		}
		
		userService.addPoints(currentUser, RATED_SOMETHING);
		if(rating.getRating() == 1)
			userService.addPoints(mapItem.getUser(), GOT_SOMETHING_RATED);
				
		return ratingCacheMapItemService.findByMapItem(mapItem);
		
	}
	
	@RequestMapping(value = "mapitem/{mapItemId}/rating/user/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public Rating getRatingByMapItemAndUser(@PathVariable Long mapItemId, @PathVariable Long userId, Locale locale, Model model) throws NotFoundException {
		
		MapItem mapItem = mapItemService.find(mapItemId) ;
		User user = userService.find(userId);
		
		if (mapItem == null)
			throw new NotFoundException("mapitem not found");
		if (user == null) 
			throw new NotFoundException("user not found");
		
		
		Rating rating = ratingService.findByUserAndMapItem(user, mapItem);
		
		if (rating == null) {
			throw new NotFoundException("rating not found");
		}
		return rating;
	}
	
	@RequestMapping(value = "mapitem/{mapItemId}/rating/user", method = RequestMethod.GET)
	@ResponseBody
	public Rating getRatingByMapItemAndCurrentUser(@PathVariable Long mapItemId, Locale locale, Model model) throws NotFoundException {
		
		MapItem mapItem = mapItemService.find(mapItemId) ;
		User user = userService.getCurrentUser();
		
		if (mapItem == null)
			throw new NotFoundException("mapitem not found");
		if (user == null) 
			throw new NotFoundException("user not found");
		
		
		Rating rating = ratingService.findByUserAndMapItem(user, mapItem);
		
		if (rating == null) {
			throw new NotFoundException("rating not found");
		}
		return rating;
	}
}
