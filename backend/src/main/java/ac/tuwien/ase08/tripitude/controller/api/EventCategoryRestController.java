package ac.tuwien.ase08.tripitude.controller.api;

import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ac.tuwien.ase08.tripitude.entity.EventCategory;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventCategoryService;

@Controller
@RequestMapping("api")
public class EventCategoryRestController {
	
	@Autowired
	IEventCategoryService categoryService;
	@Autowired 
	private MessageSource messageSource;
	
	private static final Logger logger = LoggerFactory.getLogger(EventCategoryRestController.class);
	
	@RequestMapping(value = "eventcategory", method = RequestMethod.GET)
	@ResponseBody
	public List<EventCategory> listAll(Locale locale, Model model) throws NotFoundException {
		if(categoryService.list().isEmpty()){
			throw new NotFoundException("No categories for event found.");
		}
		return categoryService.list();
	}
}
