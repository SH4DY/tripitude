package ac.tuwien.ase08.tripitude.service.interfaces;

import java.util.List;

import javassist.NotFoundException;
import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.AlreadyAttendingException;

public interface IEventService extends IGenericService<Event, Long> {

	public void attend(Event e, User u) throws AlreadyAttendingException, NotFoundException;
	
	public void unattend(Event event, User u) throws NotFoundException;
	
	public List<User> getAttendingUsers(Long id);

	public Event findFullEvent(Long id);
}
