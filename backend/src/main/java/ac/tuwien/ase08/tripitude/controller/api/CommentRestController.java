package ac.tuwien.ase08.tripitude.controller.api;

import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ac.tuwien.ase08.tripitude.entity.Comment;
import ac.tuwien.ase08.tripitude.entity.Diary;
import ac.tuwien.ase08.tripitude.entity.DiaryItem;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.exception.ForbiddenException;
import ac.tuwien.ase08.tripitude.service.interfaces.ICommentService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;
import ac.tuwien.ase08.tripitude.validator.DefaultValidator;

@Controller
@RequestMapping("api")
public class CommentRestController {

	@Autowired
	IMapItemService mapItemService;
	@Autowired
	IUserService userService;
	@Autowired
	ICommentService commentService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private DefaultValidator defaultValidator;
	@Autowired
	IDiaryItemService diaryItemService;
	@Autowired
    IDiaryService diaryService;
	

	private static final Logger logger = LoggerFactory
			.getLogger(CommentRestController.class);
	
	private static final Long COMMENTED_SOMETHING = 1L;
	private static final Long CREATED_SOMETHING_COMMENTED = 1L;

	@RequestMapping(value = "mapitem/{id}/comment", method = RequestMethod.GET)
	@ResponseBody
	public List<Comment> getCommentsByMapItem(
			@PathVariable Long id,
			Locale locale, Model model) {

		MapItem mapItem = mapItemService.findFullMapItem(id);
		List<Comment> l = mapItem.getComments();

		return l;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "mapitem/{id}/comment", method = RequestMethod.POST)
	@ResponseBody	
	public Object add(@RequestBody Comment comment, @PathVariable Long id, BindingResult result, Locale locale, Model model) throws FieldErrorException {	

		comment.setUser(userService.getCurrentUser());
		comment.setMapItem(mapItemService.find(id));

		commentService.add(comment);
		
		userService.addPoints(userService.getCurrentUser(), COMMENTED_SOMETHING);
		userService.addPoints(comment.getMapItem().getUser(), CREATED_SOMETHING_COMMENTED);
		
		return comment;
	}
	
	@RequestMapping(value = "diaryitem/{id}/comment", method = RequestMethod.POST)
	@ResponseBody	
	public Object addToDiaryItem(@RequestBody Comment comment, @PathVariable Long id, BindingResult result, Locale locale, Model model) throws FieldErrorException, ForbiddenException, NotFoundException {	

		if (userService.getCurrentUser() == null) {		
			if (comment.getName() == null || comment.getName().length() <= 0) {
				throw new ForbiddenException("comment needs at least a author name");
			}		
		}
		else {
			comment.setUser(userService.getCurrentUser());
			userService.addPoints(userService.getCurrentUser(), COMMENTED_SOMETHING);
		}
		
		DiaryItem diaryItem = diaryItemService.find(id);
		
		if (diaryItem == null) {
			throw (new NotFoundException("diaryitem not found not found"));
		}
		
		comment.setDiaryItem(diaryItem);
		commentService.add(comment);
		
		userService.addPoints(diaryItem.getDiary().getUser(), CREATED_SOMETHING_COMMENTED);
		
		return comment;
	}

	@RequestMapping(value = "makeCommentOnMapItem", method = RequestMethod.POST)
	@ResponseBody
	public void makeCommentOnMapItem(
			@RequestParam(value = "mapitem_id") Long mapItemID,
			@RequestParam(value = "user_id", defaultValue="0") Long userID,
			@RequestParam(value = "text") String text,
			Locale locale, Model model) {
		
		userService.addPoints(userService.getCurrentUser(), COMMENTED_SOMETHING);
		userService.addPoints(mapItemService.find(mapItemID).getUser(), CREATED_SOMETHING_COMMENTED);

		commentService.add(new Comment(text, mapItemService.find(mapItemID), userService.find(userID)));
	}
}
