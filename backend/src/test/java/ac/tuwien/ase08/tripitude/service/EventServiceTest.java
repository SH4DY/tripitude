package ac.tuwien.ase08.tripitude.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javassist.NotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.AlreadyAttendingException;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

public class EventServiceTest extends PrepareServiceTest {
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IEventService eventService;
	
	@Autowired
	private ICoordinateService coordinateService;
	
	@Autowired
	private IHotspotService hotspotService;
	
	private Event event;
	
	@Before
	public void setUp() {
		User u = new User("Fritz Huber", "12345", "fritz@aol.com");
		userService.add(u);
		
		Coordinate co = new Coordinate(new Double(128637), new Double(12831372));
		coordinateService.add(co);
		
		Hotspot hs = new Hotspot("Oper", "ahksd");
		hs.setCoordinate(co);
		hotspotService.add(hs);
		
		event = new Event("Wien Tour", new Date(), hs, u);
		
		eventService.add(event);
	}

	@Test
	@Transactional
	public void testAddEvent() {
		eventService.add(event);
		
		assertNotNull(event.getId());
		Event found = eventService.find(event.getId());
    	
    	assertEquals(event, found);
	}
	
	@Test
	@Transactional
	public void testFindEvent() {		
		eventService.add(event);
		Event found = eventService.find(event.getId());
		
		assertNotNull(found);

    	assertEquals(event, found);
	}
	
	@Test
	@Transactional
	public void testUpdateEvent() {
		eventService.add(event);
		event.setName(event.getName()+"+ extra Text");
		
		eventService.update(event);
		
		Event found = eventService.find(event.getId());
		
		assertEquals(event, found);
	}
	
	@Test
	@Transactional
	public void testListEvent() {
		int count = 8;
		List<Event> es = new ArrayList<Event>();
		
		for (int i = 0; i < count; i++) {
			eventService.add(event);
			es.add(event);
		}
		
		List<Event> list = new ArrayList<Event>();
		list = eventService.list();
		for(Event temp : es) {
			assertTrue(list.contains(temp));
		}
	}
	
	@Test
	@Transactional
	public void testAttend() throws NotFoundException {
		eventService.add(event);
		
		Event e = eventService.find(event.getId());
		User u = userService.find(e.getUser().getId());
		
		try {
			eventService.attend(e, u);
		} catch (AlreadyAttendingException e1) {
			fail();
			e1.printStackTrace();
		}
		
		assertTrue(eventService.find(e.getId()).getAttendingUsers().contains(u));
	}
}
