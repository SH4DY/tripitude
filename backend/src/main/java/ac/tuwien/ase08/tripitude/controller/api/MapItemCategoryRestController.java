package ac.tuwien.ase08.tripitude.controller.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.MapItemCategory;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemCategoryService;

@Controller
@RequestMapping("api")
public class MapItemCategoryRestController {
	
	@Autowired
	IMapItemCategoryService categoryService;
	@Autowired 
	private MessageSource messageSource;
	
	private static final Logger logger = LoggerFactory.getLogger(MapItemCategoryRestController.class);
	
	@RequestMapping(value = "mapitemcategory", method = RequestMethod.GET)
	@ResponseBody
	public List<MapItemCategory> list(@RequestParam(value = "title", defaultValue= "") String title,
			Locale locale, Model model) {
		
		if(!title.equals("")) {
			MapItemCategory found = categoryService.getMapItemCategoryByName(title);
			List<MapItemCategory> catlist = new ArrayList<MapItemCategory>();
			catlist.add(found);
			return catlist;
		}
		
		return categoryService.list();
	}
	
	@RequestMapping(value = "mapitemcategory/{id}", method = RequestMethod.GET)
	@ResponseBody
	public MapItemCategory get(@PathVariable Long id, Locale locale, Model model) throws NotFoundException {
		
		
		MapItemCategory cat = categoryService.find(id);
		
		if (cat == null) {
			throw(new NotFoundException("mapitemCategory not found"));
		}
		return cat;
	}
}
