package ac.tuwien.ase08.tripitude.validator;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventService;

@Component
public class CreateEventValidator implements Validator {
    
	@Autowired
	private IEventService eventService;
	
	@Override
	public boolean supports(Class<?> clazz) {
	  return User.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		Event event = (Event)target;
		
		if(event.getTime().before(new Date()))
			errors.rejectValue("time", "error.event.beforeToday", "Event cannot take place in the past");
		
		if(event.getMapItem() == null)
			errors.rejectValue("mapitem", "error.event.noMapItem", "No Map Item found");
	}

}
