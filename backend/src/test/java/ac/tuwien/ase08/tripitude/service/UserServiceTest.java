package ac.tuwien.ase08.tripitude.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.HistoryItem;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHistoryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

public class UserServiceTest extends PrepareServiceTest {
	
	@Autowired
	private IUserService userService;
	
	private User user;
	
	@Before
	public void setUp() {
		user = new User("Fritz Huber", "12345", "fritz@aol.com");
	}

	@Test
	@Transactional
	public void testAddUser() {
		userService.add(user);
		
		assertNotNull(user.getId());
		User found = userService.find(user.getId());
    	
    	assertEquals(user, found);
	}
	
	@Test
	@Transactional
	public void testFindUser() {		
		userService.add(user);
		User found = userService.find(user.getId());
		
		assertNotNull(found);

    	assertEquals(user, found);
	}
	
	@Test
	@Transactional
	public void testUpdateUser() {
		userService.add(user);
		user.setName(user.getName()+"asdk");
		
		userService.update(user);
		
		User found = userService.find(user.getId());
		
		assertEquals(user, found);
	}
	
	@Test
	@Transactional
	public void testListUser() {
		int count = 8;
		List<User> es = new ArrayList<User>();
		
		for (int i = 0; i < count; i++) {
			userService.add(user);
			es.add(user);
		}
		
		List<User> list = new ArrayList<User>();
		list = userService.list();
		for(User temp : es) {
			assertTrue(list.contains(temp));
		}
	}
}
