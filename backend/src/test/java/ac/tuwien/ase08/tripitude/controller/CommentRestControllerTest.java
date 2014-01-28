package ac.tuwien.ase08.tripitude.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ac.tuwien.ase08.tripitude.controller.api.CommentRestController;
import ac.tuwien.ase08.tripitude.entity.Comment;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Diary;
import ac.tuwien.ase08.tripitude.entity.DiaryItem;
import ac.tuwien.ase08.tripitude.entity.HistoryItem;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.exception.ForbiddenException;
import ac.tuwien.ase08.tripitude.service.PrepareServiceTest;
import ac.tuwien.ase08.tripitude.service.interfaces.ICommentService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHistoryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@ContextConfiguration("/abstract-session-test.xml")
public class CommentRestControllerTest extends PrepareServiceTest {
	
	@Autowired
	private CommentRestController commentRestController;
	@Autowired
	private IHotspotService hotspotService;
	@Autowired
	private ICommentService commentService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IMapItemService mapItemService;
	@Autowired
	private IDiaryService diaryService;
	@Autowired
	private IDiaryItemService diaryItemService;
	@Autowired
	private IHistoryItemService historyItemService;
	
	@Autowired
    private AuthenticationManager am;
	
	@Autowired
	private IRoleService roleService;
	
	private Model model;
	private Locale locale;

	private Hotspot mapItem1;
	private Hotspot mapItem2;
	private Comment comment1;
	private Comment comment2;
	private Comment comment3;
	private Comment comment4;
	private User user;	
	
	
	@Before
	@Transactional
	public void setUp() {
		model = new ExtendedModelMap();
		locale = new Locale("Austria");
		
		user = new User("Keyser Soze", "secret", "keyser@gmail.com");
		user.setRole(roleService.getRoleByRole("AUTHENTICATED"));
		user.setPoints(0L);
		userService.add(user);
		
		mapItem1 = new Hotspot("Restaurant", "Best Restaurant with lots of pizza");
		mapItem1.setCoordinate(new Coordinate(20.0, 30.0));
		mapItem1.setUser(user);
		hotspotService.add(mapItem1);

		mapItem2 = new Hotspot("Cinema", "This is a huge cinema");
		mapItem2.setCoordinate(new Coordinate(20.0, 50.0));
		mapItem2.setUser(user);
		hotspotService.add(mapItem2);
		
		comment1 = new Comment("So bad...", mapItem1, user);
		comment2 = new Comment("LAAAAME", mapItem1, user);
		comment3 = new Comment("<3", mapItem2, user);
		comment4 = new Comment("Keine österreichische Küche", mapItem2, user);
		commentService.add(comment1);
		commentService.add(comment2);
		commentService.add(comment3);
		commentService.add(comment4);
		
		Authentication auth = new UsernamePasswordAuthenticationToken("keyser@gmail.com", "secret");
		SecurityContextHolder.getContext().setAuthentication(am.authenticate(auth));
	}
	
	@Test
	@Transactional
	public void testGetCommentMyMapItem(){
		
		//Test it for MapItem1
		List<Comment> commentsByMapItem1FromRest = commentRestController.getCommentsByMapItem(mapItem1.getId(),locale, model);
		List<Comment> commentsByMapItem1 = hotspotService.findFullHotspot(mapItem1.getId()).getComments();
		assertEquals(commentsByMapItem1, commentsByMapItem1FromRest);
		
		//Test it for MapItem2
		List<Comment> commentsByMapItem2FromRest = commentRestController.getCommentsByMapItem(mapItem2.getId(),locale, model);
		List<Comment> commentsByMapItem2 = hotspotService.findFullHotspot(mapItem2.getId()).getComments();
		assertEquals(commentsByMapItem2, commentsByMapItem2FromRest);
	}
	
	@Test
	@Transactional
	public void testAddComment() throws FieldErrorException{	
		BindingResult r = null;
		String text = "testComment";
		
		Comment c1 = new Comment(text, mapItem1, user);
		commentRestController.add(c1, c1.getMapItem().getId(), r, locale, model);
		
		List<Comment> comments = commentService.list();
		
		assertTrue(comments.contains(c1));
	}
	
	@Test
	@Transactional
	public void testMakeCommentOnMapItem() {
		Comment c1 = new Comment("testComment1", mapItem1, user);
		Comment c2 = new Comment("testComment2", mapItem1, user);
		Comment c3 = new Comment("testComment3", mapItem2, user);
		Comment c4 = new Comment("testComment4", mapItem2, user);
		
		commentRestController.makeCommentOnMapItem(mapItem1.getId(), user.getId(), c1.getText(), locale, model);
		commentRestController.makeCommentOnMapItem(mapItem1.getId(), user.getId(), c2.getText(), locale, model);
		commentRestController.makeCommentOnMapItem(mapItem2.getId(), user.getId(), c3.getText(), locale, model);
		commentRestController.makeCommentOnMapItem(mapItem2.getId(), user.getId(), c4.getText(), locale, model);
		
		// hibernate.initialize() apparently not working in test context
		//List<Comment> commentsOnMapItem1 = mapItemService.findFullMapItem(mapItem1.getId()).getComments();
		//List<Comment> commentsOnMapItem2 = mapItemService.findFullMapItem(mapItem2.getId()).getComments();
		
		List<Comment> allComments = commentService.list();
		List<Comment> commentsOnMapItem1 = new ArrayList<Comment>();
		List<Comment> commentsOnMapItem2 = new ArrayList<Comment>();
		
		for (Comment c : allComments) {
//			System.out.println(c);
			if(c.getMapItem().getId() == mapItem1.getId()) commentsOnMapItem1.add(c);
			if(c.getMapItem().getId() == mapItem2.getId()) commentsOnMapItem2.add(c);
		}
		
		boolean containsC1 = false;
		boolean containsC2 = false;
		boolean containsC3 = false;
		boolean containsC4 = false;
		
		// a simple contains not possible because c1.id=null and c.id=123
		for (Comment c : commentsOnMapItem1) {
			if(c.getText().equals(c1.getText()) && c.getUser().getId() == c1.getUser().getId()) containsC1 = true;
			if(c.getText().equals(c2.getText()) && c.getUser().getId() == c2.getUser().getId()) containsC2 = true;
		}
		for (Comment c : commentsOnMapItem2) {
			if(c.getText().equals(c3.getText()) && c.getUser().getId() == c3.getUser().getId()) containsC3 = true;
			if(c.getText().equals(c4.getText()) && c.getUser().getId() == c4.getUser().getId()) containsC4 = true;
		}
		
		assertTrue(containsC1 && containsC2 && containsC3 && containsC4);
	}
	
	@Test
	@Transactional
	public void testaddToDiaryItem() throws FieldErrorException, ForbiddenException, NotFoundException {
		BindingResult r = null;
		Comment c1 = new Comment("testComment1", mapItem1, user);
		
		Diary d = new Diary("Wien Tagebuch", user);
		diaryService.add(d);
		HistoryItem hi = new HistoryItem(new Date(), mapItem1, user);
		historyItemService.add(hi);
		DiaryItem di = new DiaryItem("Rathaus Besuch", hi, d);
		diaryItemService.add(di);
		
		commentRestController.addToDiaryItem(c1, di.getId(), r, locale, model);
		
		assertTrue(commentService.list().contains(c1));
	}
	
	@Test(expected=NotFoundException.class)
	@Transactional
	public void testaddToDiaryItemThrowsNotFoundException() throws FieldErrorException, ForbiddenException, NotFoundException {
		BindingResult r = null;
		Comment c1 = new Comment("testComment1", mapItem1, user);
		
		commentRestController.addToDiaryItem(c1, new Long(-13), r, locale, model);
	}
}
