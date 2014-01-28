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
import ac.tuwien.ase08.tripitude.entity.Diary;
import ac.tuwien.ase08.tripitude.entity.DiaryItem;
import ac.tuwien.ase08.tripitude.entity.HistoryItem;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.ICommentService;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHistoryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

public class DiaryItemServiceTest extends PrepareServiceTest {
	
	@Autowired
	private ICommentService commentService;
	
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
	
	@Autowired
	private IHistoryItemService historyItemService;
	
	private DiaryItem diaryItem;
	
	@Before
	public void setUp() {
		User u = new User("Fritz Huber", "12345", "fritz@aol.com");
		userService.add(u);
		
		Coordinate co = new Coordinate(new Double(128637), new Double(12831372));
		coordinateService.add(co);
		
		Hotspot hs = new Hotspot("Oper", "ahksd");
		hs.setCoordinate(co);
		hotspotService.add(hs);
		
		HistoryItem hi = new HistoryItem(new Date(), hs, u);
		historyItemService.add(hi);
		
		Diary d = new Diary("Wien WE", u);
		diaryService.add(d);
		
		diaryItem = new DiaryItem("Opern Besuch", hi, d);
	}

	@Test
	@Transactional
	public void testAddDiaryItem() {
		diaryItemService.add(diaryItem);
		
		assertNotNull(diaryItem.getId());
    	DiaryItem found = diaryItemService.find(diaryItem.getId());
    	
    	assertEquals(diaryItem, found);
	}
	
	@Test
	@Transactional
	public void testFindDiaryItem() {		
		diaryItemService.add(diaryItem);
		DiaryItem found = diaryItemService.find(diaryItem.getId());
		
		assertNotNull(found);

    	assertEquals(diaryItem, found);
	}
	
	@Test
	@Transactional
	public void testUpdateDiaryItem() {
		diaryItemService.add(diaryItem);
		diaryItem.setText(diaryItem.getText()+"+ extra Text");
		
		diaryItemService.update(diaryItem);
		
		DiaryItem found = diaryItemService.find(diaryItem.getId());
		
		assertEquals(diaryItem, found);
	}
	
	@Test
	@Transactional
	public void testListDiaryItem() {
		int count = 8;
		List<DiaryItem> dis = new ArrayList<DiaryItem>();
		
		for (int i = 0; i < count; i++) {
			diaryItemService.add(diaryItem);
			dis.add(diaryItem);
		}
		
		List<DiaryItem> list = new ArrayList<DiaryItem>();
		list = diaryItemService.list();
		for(DiaryItem temp : dis) {
			assertTrue(list.contains(temp));
		}
	}
}
