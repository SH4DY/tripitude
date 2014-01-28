package ac.tuwien.ase08.tripitude.controller;

import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ac.tuwien.ase08.tripitude.controller.api.MapItemCategoryRestController;
import ac.tuwien.ase08.tripitude.entity.MapItemCategory;
import ac.tuwien.ase08.tripitude.service.PrepareServiceTest;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemCategoryService;

@ContextConfiguration("/abstract-session-test.xml")
public class MapItemCategoryRestControllerTest extends PrepareServiceTest {
	
	@Autowired
	private MapItemCategoryRestController categoryController;
	
	@Autowired
	private IMapItemCategoryService categoryService;
	
	private Model model;
	private Locale locale;
	
	private MapItemCategory category1 = null, category2 = null;
	
	@Before
	@Transactional
	public void setUp() {
		model = new ExtendedModelMap();
		locale = new Locale("Austria");
		
		
		category1 = new MapItemCategory("Restaurant");
		category2 = new MapItemCategory("Cinema");
		
		categoryService.add(category1);
		categoryService.add(category2);
		
	}
	
	@Test
	@Transactional
	public void testListAll() {
		
		List<MapItemCategory> listFromService = categoryService.list();
		List<MapItemCategory> listFromController = categoryController.list("", locale, model);
		
		assertEquals(listFromService, listFromController);
		
	}
	
	@Test
	@Transactional
	public void testListAllByTitle() {
		List<MapItemCategory> listFromController = categoryController.list(category1.getName(), locale, model);
		
		assertTrue(listFromController.contains(category1));
	}
	
	@Test
	@Transactional
	public void testGetById() {
		MapItemCategory found = null;
		try {
			found = categoryController.get(category1.getId(), locale, model);
		} catch (NotFoundException e) {
			fail();
			e.printStackTrace();
		}
		assertEquals(found, category1);
	}
	
	@Test(expected = NotFoundException.class)
	@Transactional
	public void testGetByIdException() throws NotFoundException {
		MapItemCategory found = categoryController.get(-1L, locale, model);
		
		fail();
	}

}
