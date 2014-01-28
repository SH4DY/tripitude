package ac.tuwien.ase08.tripitude.controller.api;

import java.util.List;
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

import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.AlreadyAttendingException;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;
import ac.tuwien.ase08.tripitude.validator.CreateEventValidator;

@Controller
@RequestMapping("api")
public class EventRestController {

	@Autowired
	private IEventService eventService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IEventCategoryService eventCategoryService;
	@Autowired  
    private CreateEventValidator createEventValidator;
	
	private static final Long CREATED_SOMETHING = 10L;
	private static final Long DID_SOMETHING = 5L;
	private static final Long CREATED_SOMETHING_GOOD = 10L;
	
	private static final Logger logger = LoggerFactory.getLogger(EventRestController.class);
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "event", method = RequestMethod.POST)
	@ResponseBody
	public Object create(@Valid @RequestBody Event event, BindingResult result, Locale locale, Model model) throws FieldErrorException {
		
		createEventValidator.validate(event, result);
		if(result.hasErrors()){
			throw new FieldErrorException(result.getFieldErrors());
		}
		
		eventService.add(event);	
		userService.addPoints(userService.getCurrentUser(), CREATED_SOMETHING);
		return event;
	}
	
	@RequestMapping(value = "event/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Event get(@PathVariable Long id, Locale locale, Model model) throws NotFoundException {
		
		Event e = eventService.findFullEvent(id);
		
		if (e == null) {
			throw new NotFoundException("event not found");
		}
		
		return e;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "event/{id}/attend", method = RequestMethod.POST)
	@ResponseBody	
	public Object attend(@PathVariable Long id, Locale locale, Model model) throws FieldErrorException, NotFoundException {	
		Event event = eventService.find(id);
		
		if(event == null)
			throw new NotFoundException("event not found");
		
		try {
			eventService.attend(event, userService.getCurrentUser());
		} catch (AlreadyAttendingException e) {
			throw new FieldErrorException("403 Forbidden " + e.getMessage());
		}
		
		userService.addPoints(userService.getCurrentUser(), DID_SOMETHING);
		userService.addPoints(event.getUser(), CREATED_SOMETHING_GOOD);
		
		return true;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "event/{id}/unattend", method = RequestMethod.POST)
	@ResponseBody	
	public Object unattend(@PathVariable Long id, Locale locale, Model model) throws FieldErrorException, NotFoundException {	
		Event event = eventService.find(id);
		
		if(event == null)
			throw new NotFoundException("event not found");
		
		eventService.unattend(event, userService.getCurrentUser());	
		
		userService.addPoints(userService.getCurrentUser(), -DID_SOMETHING);
		userService.addPoints(event.getUser(), -CREATED_SOMETHING_GOOD);
		
		return true;
	}
	
	@RequestMapping(value = "attendingUsers/{id}", method = RequestMethod.GET)
	@ResponseBody
	public List<User> getAttendingUsers(@PathVariable Long id, Locale locale, Model model) throws NotFoundException {
		Event event = eventService.find(id);
		
		if(event == null)
			throw new NotFoundException("event not found");
		
		List<User> attendingUsers = eventService.getAttendingUsers(event.getId());
		
		return attendingUsers;
	}
}
