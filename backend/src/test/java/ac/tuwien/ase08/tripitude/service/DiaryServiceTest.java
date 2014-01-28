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

import ac.tuwien.ase08.tripitude.entity.Diary;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

public class DiaryServiceTest extends PrepareServiceTest {
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IDiaryService diaryService;
	
	private Diary diary;
	
	@Before
	public void setUp() {
		User u = new User("Fritz Huber", "12345", "fritz@aol.com");
		userService.add(u);
		
		diary = new Diary("Wien WE", u);
		
		diaryService.add(diary);
	}

	@Test
	@Transactional
	public void testAddDiary() {
		diaryService.add(diary);
		
		assertNotNull(diary.getId());
    	Diary found = diaryService.find(diary.getId());
    	
    	assertEquals(diary, found);
	}
	
	@Test
	@Transactional
	public void testFindDiary() {		
		diaryService.add(diary);
		Diary found = diaryService.find(diary.getId());
		
		assertNotNull(found);

    	assertEquals(diary, found);
	}
	
	@Test
	@Transactional
	public void testUpdateDiary() {
		diaryService.add(diary);
		diary.setName(diary.getName()+"+ extra Text");
		
		diaryService.update(diary);
		
		Diary found = diaryService.find(diary.getId());
		
		assertEquals(diary, found);
	}
	
	@Test
	@Transactional
	public void testListDiary() {
		int count = 8;
		List<Diary> ds = new ArrayList<Diary>();
		
		for (int i = 0; i < count; i++) {
			diaryService.add(diary);
			ds.add(diary);
		}
		
		List<Diary> list = new ArrayList<Diary>();
		list = diaryService.list();
		for(Diary temp : ds) {
			assertTrue(list.contains(temp));
		}
	}
}
