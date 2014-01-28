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
import ac.tuwien.ase08.tripitude.entity.DiaryItem;
import ac.tuwien.ase08.tripitude.entity.EventCategory;
import ac.tuwien.ase08.tripitude.entity.HistoryItem;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHistoryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

public class HistoryItemServiceTest extends PrepareServiceTest {
	@Autowired
	private IHistoryItemService historyItemService;
	
	@Autowired
	private ICoordinateService coordinateService;
	
	@Autowired
	private IHotspotService hotspotService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IDiaryItemService diaryItemService;
	
	@Autowired
	private IDiaryService diaryService;
	
	private HistoryItem historyItem;
	
	@Before
	public void setUp() {
		User u = new User("Fritz Huber", "12345", "fritz@aol.com");
		userService.add(u);
		
		Coordinate co = new Coordinate(new Double(128637), new Double(12831372));
		coordinateService.add(co);
		
		Hotspot hs = new Hotspot("Oper", "ahksd");
		hs.setCoordinate(co);
		hotspotService.add(hs);
		
		historyItem = new HistoryItem(new Date(), hs, u);
		
		historyItemService.add(historyItem);
	}

	@Test
	@Transactional
	public void testAddHistoryItem() {
		historyItemService.add(historyItem);
		
		assertNotNull(historyItem.getId());
		HistoryItem found = historyItemService.find(historyItem.getId());
    	
    	assertEquals(historyItem, found);
	}
	
	@Test
	@Transactional
	public void testFindHistoryItem() {		
		historyItemService.add(historyItem);
		HistoryItem found = historyItemService.find(historyItem.getId());
		
		assertNotNull(found);

    	assertEquals(historyItem, found);
	}
	
	@Test
	@Transactional
	public void testUpdateHistoryItem() {
		historyItemService.add(historyItem);
		historyItem.setTime(new Date());
		
		historyItemService.update(historyItem);
		
		HistoryItem found = historyItemService.find(historyItem.getId());
		
		assertEquals(historyItem, found);
	}
	
	@Test
	@Transactional
	public void testListHistoryItem() {
		int count = 8;
		List<HistoryItem> es = new ArrayList<HistoryItem>();
		
		for (int i = 0; i < count; i++) {
			historyItemService.add(historyItem);
			es.add(historyItem);
		}
		
		List<HistoryItem> list = new ArrayList<HistoryItem>();
		list = historyItemService.list();
		for(HistoryItem temp : es) {
			assertTrue(list.contains(temp));
		}
	}
}
