package ac.tuwien.ase08.tripitude.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.entity.Comment;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.ICommentService;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

public class CommentServiceTest extends PrepareServiceTest {

	@Autowired
	private ICommentService commentService;
	
	@Autowired
	private ICoordinateService coordinateService;
	
	@Autowired
	private IHotspotService hotspotService;
	
	@Autowired
	private IUserService userService;
	
	private Comment comment = null;
	private Coordinate coord = null;
	private Hotspot hotspot = null;
	private User user = null;
	List<Comment> commentlist = null;
	
	@Before
	public void setUp() {

		coord = new Coordinate(15D, 25D);
		coordinateService.add(coord);
		
		hotspot = new Hotspot("hotspot1", "desc", 0D);
		hotspot.setCoordinate(coord);
		hotspotService.add(hotspot);
		
		user = new User("Keyser Soze", "secret", "keyser@gmail.com");
		userService.add(user);
				
		comment = new Comment("Nice Hotspot");
		comment.setMapItem(hotspot);
		comment.setUser(user);
				
		commentlist = Arrays.asList(
				new Comment("inb4 first"), 
				new Comment("first"), 
				new Comment("lol"));
		
	}

	@Test
	@Transactional
	public void testAddComment() {
		
		commentService.add(comment);
    	assertNotNull(comment.getId());
    	Comment found = commentService.find(comment.getId());
    	
    	assertEquals(comment, found);
	}
	
	@Test
	@Transactional
	public void testFindComment() {
				
		commentService.add(comment);		
		Comment found = commentService.find(comment.getId());
		
		assertNotNull(found);

    	assertEquals(found.getUser(), comment.getUser());
    	assertEquals(found.getMapItem(), comment.getMapItem());
	}
	
	@Test
	@Transactional
	public void testUpdateComment() {
				
		commentService.add(comment);		
		comment.setText("Updated Comment");
		
		Comment found = commentService.find(comment.getId());
		
		assertEquals(found.getMapItem(), comment.getMapItem());
	}
	
	@Test
	@Transactional
	public void testListComment() {
		
		assertEquals(0, commentService.list().size());
		
		for (Comment temp : commentlist) {
			temp.setUser(user);
			temp.setMapItem(hotspot);			
			
			commentService.add(temp);
		}
		
		List<Comment> found = commentService.list();
		assertEquals(3, found.size());
		for (Comment coord : found){
			assertTrue(commentlist.contains(coord));
		}
	}
}
