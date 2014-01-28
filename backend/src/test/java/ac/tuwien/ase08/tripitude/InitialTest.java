package ac.tuwien.ase08.tripitude;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.CoordinateDAO;
import ac.tuwien.ase08.tripitude.dao.interfaces.ICoordinateDAO;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Role;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml")
public class InitialTest {
    
	@Autowired
	private IUserService userService;
	@Autowired 
	private IRoleService roleService;
	@Autowired
	private ICoordinateDAO coordinateDao;

	@Test
	public void testAddUser() {
		Role r = new Role();
		r.setRole("AUTHENTICATED");		
		roleService.add(r);
		
		Role newRole = roleService.getRoleByRole("AUTHENTICATED");
		
		assertTrue(newRole != null);
		
	}


	
}
