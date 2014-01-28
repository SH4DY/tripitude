package ac.tuwien.ase08.tripitude.controller;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ac.tuwien.ase08.tripitude.controller.api.EventCategoryRestController;
import ac.tuwien.ase08.tripitude.entity.EventCategory;
import ac.tuwien.ase08.tripitude.service.PrepareServiceTest;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventCategoryService;

@ContextConfiguration("/abstract-session-test.xml")
public class EventCategoryRestControllerTest extends PrepareServiceTest {
	
	@Autowired
	private EventCategoryRestController eventCatController;
	
	@Autowired
	private IEventCategoryService eventCatService;
	
	private Model model;
	private Locale locale;
	
	private EventCategory category1 = null, category2 = null;
	
	@Before
	@Transactional
	public void setUp() {
		model = new ExtendedModelMap();
		locale = new Locale("Austria");
	}
	
	@Test
	@Transactional
	public void testListAll() {
		category1 = new EventCategory("Restaurant");
		category2 = new EventCategory("Cinema");
		eventCatService.add(category1);
		eventCatService.add(category2);
		
		List<EventCategory> listFromController = null;
		try {
			listFromController = eventCatController.listAll(locale, model);
		} catch (NotFoundException e) {
			fail();
			e.printStackTrace();
		}
		
		assertNotNull(listFromController);
		assertEquals(listFromController.size(), 2);
		assertTrue(listFromController.contains(category1));
		assertTrue(listFromController.contains(category2));
	}
	
	@Test(expected=NotFoundException.class)
	@Transactional
	public void testListAllThrowsNotFoundException() throws NotFoundException {
		eventCatController.listAll(locale, model);
	}

}
