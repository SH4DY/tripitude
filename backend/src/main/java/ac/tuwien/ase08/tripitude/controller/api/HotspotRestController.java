package ac.tuwien.ase08.tripitude.controller.api;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ac.tuwien.ase08.tripitude.entity.HistoryItem;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.search.MapItemSearchCriteria;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHistoryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;
import ac.tuwien.ase08.tripitude.validator.DefaultValidator;

@Controller
@RequestMapping("api")
public class HotspotRestController {
	
	@Autowired
	private IHotspotService hotspotService;
	@Autowired
	private ICoordinateService coordinateService;
	@Autowired
	private IUserService userService;
	@Autowired 
	private MessageSource messageSource;
	@Autowired
	private DefaultValidator defaultValidator;
	@Autowired
	private IHistoryItemService historyItemService;
	
	private static final Long CREATED_SOMETHING = 50L;
	
	//private static final Logger logger = LoggerFactory.getLogger(HotspotRestController.class);
	
	@RequestMapping(value = "hotspot", method = RequestMethod.GET)
	@ResponseBody
	public List<MapItem> list(@RequestParam(value = "ids", defaultValue = "") String ids,
			                  @RequestParam(value = "category_ids", defaultValue = "") String categoryIds,
			                  @RequestParam(value = "title_like", defaultValue = "") String titleLike,
			                  @RequestParam(value = "operator", defaultValue = "") String operator,
			                  Locale locale, Model model) {
		
				
		
		List<MapItem> l = null;
		
		//create search criteria
		MapItemSearchCriteria msc = new MapItemSearchCriteria();
		msc.setIds(ids);
		msc.setCategoryIds(categoryIds);
		msc.setTitlelike(titleLike);
		msc.setOperator(operator);
		
		l = hotspotService.findByVariousCriteria(msc);
		
		return l;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "hotspot", method = RequestMethod.POST)
	@ResponseBody
	public Object create(@Valid @RequestBody Hotspot hotspot, BindingResult result, Locale locale, Model model) throws FieldErrorException {	
		hotspot.setUser(userService.getCurrentUser());	
		hotspotService.add(hotspot);
		userService.addPoints(userService.getCurrentUser(), CREATED_SOMETHING);
		historyItemService.add(new HistoryItem(new Date(), hotspot, userService.getCurrentUser()));
		return hotspot;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "hotspot", method = RequestMethod.PUT)
	@ResponseBody
	public Hotspot update(@Valid @RequestBody Hotspot hotspot, BindingResult result, Locale locale, Model model) throws FieldErrorException {	
		hotspotService.update(hotspot);
		return hotspot;
	}
	
	@RequestMapping(value = "hotspot/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Hotspot get(@PathVariable Long id, Locale locale, Model model) throws NotFoundException {
		
		Hotspot h = hotspotService.findFullHotspot(id);
		
		if (h == null) {
			throw new NotFoundException("route not found");
		}
		
		return h;
	}


}
