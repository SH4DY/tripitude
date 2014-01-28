package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import javassist.NotFoundException;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.IEventDAO;
import ac.tuwien.ase08.tripitude.dao.interfaces.IUserDAO;
import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.AlreadyAttendingException;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("eventService")
public class EventService implements IEventService {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private IEventDAO eventDAO;
	
	@Autowired
	private IUserDAO userDAO;
	
	@Autowired
	private IUserService userService;
	
	private static final Logger logger = LoggerFactory
			.getLogger(EventService.class);
	
	public void add(Event entity) {
		eventDAO.add(entity);		
	}

	public void update(Event entity) {
		eventDAO.update(entity);		
	}

	public void remove(Event entity) {
		eventDAO.remove(entity);		
	}

	public Event find(Long key) {
		return eventDAO.find(key);
	}

	public List<Event> list() {
		return eventDAO.list();
	}
	
	public Event findFullEvent(Long id){
		Event e = find(id);
		if (e == null) {
			return e;
		}
		Hibernate.initialize(e.getCategories());
		return e;
	}
	
	public void attend(Event e, User u) throws AlreadyAttendingException, NotFoundException {
		Event event = find(e.getId());
		User user = userService.find(u.getId());
		
		if(event != null)
			Hibernate.initialize(event.getAttendingUsers());
		else
			throw new NotFoundException("Event ID " + e.getId() + " called " + e.getName() + " not found.");
		if(user != null)
			Hibernate.initialize(user.getAttendingEvents());
		else
			throw new NotFoundException("User ID " + u.getId() + " named " + u.getName() + " not found.");

		if(event.getAttendingUsers().contains(u)){
			throw new AlreadyAttendingException("User " + user.getName() + " already attends to " + event.getName());
		}
		
		List<User> attendingUsers = event.getAttendingUsers();
		attendingUsers.add(user);
		event.setAttendingUsers(attendingUsers);
		
		update(event);
	}

	public void unattend(Event e, User u) throws NotFoundException{
		Event event = find(e.getId());
		User user = userService.find(u.getId());
		
		if(event != null)
			Hibernate.initialize(event.getAttendingUsers());
		else
			throw new NotFoundException("Event ID " + e.getId() + " called " + e.getName() + " not found.");
		if(user != null)
			Hibernate.initialize(user.getAttendingEvents());
		else
			throw new NotFoundException("User ID " + u.getId() + " named " + u.getName() + " not found.");
		
		event.getAttendingUsers().remove(user);
	}
	
	public List<User> getAttendingUsers(Long id){
		Query query = sessionFactory.getCurrentSession().createQuery(
				"SELECT u FROM User AS u LEFT JOIN u.attendingEvents AS ae WHERE ae.id = :id");
		query.setParameter("id", id);
		
		List<User> l = (List<User>)query.list();
		
		return l;
	}

}
