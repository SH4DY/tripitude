package ac.tuwien.ase08.tripitude.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.entity.EventCategory;
import ac.tuwien.ase08.tripitude.entity.File;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IFileService;

public class EventCategoryServiceTest extends PrepareServiceTest {
	@Autowired
	private IEventCategoryService eventCategoryService;
	
	private EventCategory category;
	
	@Before
	public void setUp() {
		category = new EventCategory("Restaurant");
		
		eventCategoryService.add(category);
	}

	@Test
	@Transactional
	public void testAddEventCategory() {
		eventCategoryService.add(category);
		
		assertNotNull(category.getId());
		EventCategory found = eventCategoryService.find(category.getId());
    	
    	assertEquals(category, found);
	}
	
	@Test
	@Transactional
	public void testFindEventCategory() {		
		eventCategoryService.add(category);
		EventCategory found = eventCategoryService.find(category.getId());
		
		assertNotNull(found);

    	assertEquals(category, found);
	}
	
	@Test
	@Transactional
	public void testUpdateEventCategory() {
		eventCategoryService.add(category);
		category.setName(category.getName()+"+ extra Text");
		
		eventCategoryService.update(category);
		
		EventCategory found = eventCategoryService.find(category.getId());
		
		assertEquals(category, found);
	}
	
	@Test
	@Transactional
	public void testListEventCategory() {
		int count = 8;
		List<EventCategory> es = new ArrayList<EventCategory>();
		
		for (int i = 0; i < count; i++) {
			eventCategoryService.add(category);
			es.add(category);
		}
		
		List<EventCategory> list = new ArrayList<EventCategory>();
		list = eventCategoryService.list();
		for(EventCategory temp : es) {
			assertTrue(list.contains(temp));
		}
	}
}
