package ac.tuwien.ase08.tripitude.controller.api;

import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ac.tuwien.ase08.tripitude.entity.HistoryItem;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IHistoryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@Controller
@RequestMapping("api")
public class HistoryItemRestController {
	
	@Autowired
	private IHistoryItemService historyItemService;
	
	@Autowired
	private IUserService userService;
	
	private static final Logger logger = LoggerFactory
			.getLogger(HistoryItemRestController.class);
	
	@RequestMapping(value = "user/{id}/historyitems", method = RequestMethod.GET)
	@ResponseBody
	public List<HistoryItem> getHistoryByUser(
			@PathVariable Long id,
			Locale locale, Model model) {
		
		User user = userService.find(id);			
		
		List<HistoryItem> userHistory = historyItemService.getHistoryByUser(user);
		
		return userHistory;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "user/historyitems", method = RequestMethod.GET)
	@ResponseBody
	public  List<HistoryItem> getHistoryByCurrentUser(
			Locale locale, Model model) {
				
		List<HistoryItem> userHistory = historyItemService.getHistoryByUser(userService.getCurrentUser());
		
		return userHistory;
	}
	
	@RequestMapping(value = "historyitem/{id}", method = RequestMethod.GET)
	@ResponseBody
	public HistoryItem getHistoryItem(@PathVariable Long id,Locale locale, Model model) throws NotFoundException {
				
		HistoryItem h = historyItemService.find(id);
		
		if (h == null) {
			throw (new NotFoundException("historyitem not found"));
		}
		return h;
	}
	
}
