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
import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.File;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventService;
import ac.tuwien.ase08.tripitude.service.interfaces.IFileService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

public class FileServiceTest extends PrepareServiceTest {
	
	@Autowired
	private IFileService fileService;
	
	private File file;
	
	@Before
	public void setUp() {
		file = new File("irgendwo liegt des");
		
		fileService.add(file);
	}

	@Test
	@Transactional
	public void testAddFile() {
		fileService.add(file);
		
		assertNotNull(file.getId());
		File found = fileService.find(file.getId());
    	
    	assertEquals(file, found);
	}
	
	@Test
	@Transactional
	public void testFindFile() {		
		fileService.add(file);
		File found = fileService.find(file.getId());
		
		assertNotNull(found);

    	assertEquals(file, found);
	}
	
	@Test
	@Transactional
	public void testUpdateFile() {
		fileService.add(file);
		file.setLocation(file.getLocation()+"+ extra Text");
		
		fileService.update(file);
		
		File found = fileService.find(file.getId());
		
		assertEquals(file, found);
	}
	
	@Test
	@Transactional
	public void testListFile() {
		int count = 8;
		List<File> es = new ArrayList<File>();
		
		for (int i = 0; i < count; i++) {
			fileService.add(file);
			es.add(file);
		}
		
		List<File> list = new ArrayList<File>();
		list = fileService.list();
		for(File temp : es) {
			assertTrue(list.contains(temp));
		}
	}
}
