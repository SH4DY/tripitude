package ac.tuwien.ase08.tripitude.controller;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javassist.NotFoundException;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ac.tuwien.ase08.tripitude.dataimport.DataImport;
import ac.tuwien.ase08.tripitude.dataimport.SimpleGPXParser;
import ac.tuwien.ase08.tripitude.entity.Diary;
import ac.tuwien.ase08.tripitude.entity.Role;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.ForbiddenException;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHistoryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired	
    private IHotspotService hotspotService;
	@Autowired
	private IUserService userService;
	@Autowired 
	private IRoleService roleService;
    @Autowired
    private IMapItemCategoryService mapItemCategoryService;
    @Autowired
    private IHistoryItemService historyItemService;
    @Autowired
    IDiaryService diaryService;
	@Autowired	
    private DataImport dataImport;


	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) throws IOException {

		User currentUser = userService.getCurrentUser();

		if (currentUser != null) {
			logger.info("username:" + currentUser.toString());
			model.addAttribute("currentUserName", currentUser.getName());
			model.addAttribute("currentUserEmail", currentUser.getEmail());			
		}
				
		return "home";	
	}
	
	
	@RequestMapping(value = "/createuser", method = RequestMethod.GET)
	public String createUser(Locale locale, Model model) throws IOException {
		
		Role r = new Role();
		r.setRole("AUTHENTICATED");
		
		roleService.add(r);
		
		User u = new User();
		u.setName("martin");
		u.setEmail("test@test.at");
		u.setPassword("nitram");
		
		u.setRole(r);
		
		try {
			userService.add(u);
		}
		catch (ConstraintViolationException ex) {
			logger.info(ex.getMessage());
		}
		
		return "home";	
	}
	
	@RequestMapping(value = "/map", method = RequestMethod.GET)
	public String map(Locale locale, Model model) throws IOException {
		
		model.addAttribute("mapItemCategories", mapItemCategoryService.list());
		model.addAttribute("currentUser", userService.getCurrentUser());
		
		return "map";
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "/diary", method = RequestMethod.GET)
	public String diary(Locale locale, Model model) throws IOException {
		
		model.addAttribute("mapItemCategories", mapItemCategoryService.list());
		model.addAttribute("currentUser", userService.getCurrentUser());
				
		return "diary";
	}
	
	@RequestMapping(value = "/diary/{id}/{accessHash}", method = RequestMethod.GET)
	public String diaryPublic(@PathVariable Long id, @PathVariable String accessHash, Locale locale, Model model, RedirectAttributes redirectAttributes) throws NotFoundException, ForbiddenException {

		//generate access hash	
		model.addAttribute("historyItems", historyItemService.getHistoryByUser(userService.getCurrentUser()));
		
		Diary diary = diaryService.findFull(id);

		if (diary == null || !diaryService.verifyAccessHash(diary.getId(), accessHash)) {
			redirectAttributes.addFlashAttribute("warningMessage", "Sorry you have no access to this trip diary");	
			return "redirect:/";
		}
		
		model.addAttribute("diaryCreator", diary.getUser().getName());
		model.addAttribute("publicDiaryId", diary.getId());
		model.addAttribute("accessHash", accessHash);
		model.addAttribute("publicMode", true);
		
		return "diary";
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "/secure", method = RequestMethod.GET)
	public String securePage(Locale locale, Model model) throws IOException {
		return "secure";
	}
	
	
	/**
	 * Add initial user and role
	 * @param locale
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/initial_data", method = RequestMethod.GET)
	public String createInitialData(Locale locale, Model model, RedirectAttributes redirectAttributes) throws IOException {
		
		
		String message = "";
		message = dataImport.importInitialUserData();
		message += dataImport.importInitialMapItemCategories();
		message += dataImport.importInitialHotspots();		
		message += dataImport.importInitialRouteData();		
		message += dataImport.importInitialEventCategories();
		message += dataImport.importInitialEvents();
		message += dataImport.importInitialComments();		
		message += dataImport.importInitialHistoryItems();
		
		
		
		redirectAttributes.addFlashAttribute("successMessage", message);
		return "redirect:/";	
	}
	
	/**
	 * Add initial user and role
	 * @param locale
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/initial_route_data", method = RequestMethod.GET)
	public String createInitialRouteData(Locale locale, Model model, RedirectAttributes redirectAttributes) throws IOException {
		
		SimpleGPXParser parser = new SimpleGPXParser();
		
		File file = null;
		try {
			file = new ClassPathResource("dataimport/route_1.gpx").getFile();
			parser.parseGPXHotspots(file);
		}
		catch (Exception e) {
			
		}
		
		
		
		return "redirect:/";	
	}

}
