package ac.tuwien.ase08.tripitude.controller;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;

import ac.tuwien.ase08.tripitude.controller.api.DiaryRestController;
import ac.tuwien.ase08.tripitude.controller.api.EventCategoryRestController;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Diary;
import ac.tuwien.ase08.tripitude.entity.DiaryItem;
import ac.tuwien.ase08.tripitude.entity.EventCategory;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.Route;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.exception.ForbiddenException;
import ac.tuwien.ase08.tripitude.service.PrepareServiceTest;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@ContextConfiguration("/abstract-session-test.xml")
public class DiaryRestControllerTest extends PrepareServiceTest {
	
	@Autowired
	private DiaryRestController diaryRestController;
	@Autowired
	private IDiaryService diaryService;
	@Autowired
	private IDiaryItemService diaryItemService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private AuthenticationManager am;
	
	private Model model;
	private Locale locale;
	private BeanPropertyBindingResult result;

	private EventCategory category1 = null, category2 = null;
	private Diary diary = null;
	private DiaryItem item1 = null;
	private DiaryItem item2 = null;
	private DiaryItem item3 = null;
	private DiaryItem item4 = null;
	private DiaryItem item5 = null;
	private DiaryItem item6 = null;
	private DiaryItem item7 = null;
	private User user = null;
	
	protected MockHttpServletRequest request;
	private MockHttpSession session;
	
	@Before
	@Transactional
	public void setUp() {
		model = new ExtendedModelMap();
		locale = new Locale("Austria");
		result = new BeanPropertyBindingResult(diary, "Errors");

		user = new User("Keyser Soze", "secret", "keyser@gmail.com");
		user.setRole(roleService.getRoleByRole("AUTHENTICATED"));
		userService.add(user);

		Authentication auth = new UsernamePasswordAuthenticationToken(
				"keyser@gmail.com", "secret");
		SecurityContextHolder.getContext().setAuthentication(
				am.authenticate(auth));
	}
	
	@Test
	@Transactional
	public void testGetDiaryById() {
		diary = new Diary("Diary", user);
		diaryService.add(diary);
		List<Diary> diaries = diaryService.findByUser(user);
		Long expectedId = diaries.get(0).getId();

		
		Diary responseDiary = null;
		try {
			responseDiary = diaryRestController.getDiary(diaries.get(0).getId(), locale, model);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ForbiddenException e) {
			e.printStackTrace();
		}
		assertEquals(expectedId, responseDiary.getId());
	}
	
	@Test
	@Transactional
	public void testAddDiary() throws NotFoundException {
		Diary diary = new Diary("Diary Vienna", user);
		Diary returnedDiary = null;
		try {
			returnedDiary = (Diary) diaryRestController.addDiary(diary, result, locale, model);
		} catch (FieldErrorException e) {
			e.printStackTrace();
		}
		
		assertEquals(diary.getName(), diaryService.find(returnedDiary.getId()).getName());
	}

	@Test
	@Transactional
	public void testDeleteItem() throws NotFoundException {
		startSession();
		startRequest();
		
		Diary diary = new Diary("Diary Vienna", user);
		Diary returnedDiary = null;
		diaryService.add(diary);
		List<Diary> diaries = diaryService.findByUser(user);
		Long idOfNewDiaryItem = diaries.get(0).getId();
		try{
			diaryRestController.deleteDiary(diaries.get(0).getId(), request, locale, model);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FieldErrorException e) {
			e.printStackTrace();
		} catch (ForbiddenException e) {
			e.printStackTrace();
		}finally{
			endRequest();
			endSession();
		}
		assertEquals(diaryService.findFull(idOfNewDiaryItem), null);
		
	}
	
	//helper methods for session and encoding
		protected void startSession() {
	        session = new MockHttpSession();
	    }

		protected void endSession() {
	        session.clearAttributes();
	        session = null;
	    }

		protected void startRequest() {
	        request = new MockHttpServletRequest();
	        request.setSession(session);
	        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	    }

		protected void endRequest() {
	        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).requestCompleted();
	        RequestContextHolder.resetRequestAttributes();
	        request = null;
	    }
	    
		public static String encodeStringSH1(String string) {
			ShaPasswordEncoder encoder = new ShaPasswordEncoder();
			return encoder.encodePassword(string, "qsease08"); 
		} 
		
}
