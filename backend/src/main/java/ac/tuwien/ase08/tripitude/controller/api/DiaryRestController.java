package ac.tuwien.ase08.tripitude.controller.api;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import javassist.NotFoundException;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ac.tuwien.ase08.tripitude.entity.Comment;
import ac.tuwien.ase08.tripitude.entity.Diary;
import ac.tuwien.ase08.tripitude.entity.DiaryItem;
import ac.tuwien.ase08.tripitude.entity.File;
import ac.tuwien.ase08.tripitude.entity.HistoryItem;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.exception.ForbiddenException;
import ac.tuwien.ase08.tripitude.service.interfaces.ICommentService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IFileService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHistoryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;
import ac.tuwien.ase08.tripitude.validator.DefaultValidator;

@Controller
@RequestMapping("api")
public class DiaryRestController {
	
	@Autowired
	IMapItemService mapItemService;
	@Autowired
	IUserService userService;
    @Autowired
    IDiaryService diaryService;
    @Autowired
    IDiaryItemService diaryItemService;
    @Autowired
    IHistoryItemService historyItemService;
	@Autowired
	IFileService fileService;
	@Autowired
	ICommentService commentService;
	
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private DefaultValidator defaultValidator;
	

	private static final Logger logger = LoggerFactory
			.getLogger(CommentRestController.class);
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "diary/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Diary getDiary(@PathVariable Long id, Locale locale, Model model) throws NotFoundException, ForbiddenException {

		Diary diary = diaryService.findFull(id);
		
		if (diary == null) {
			throw (new NotFoundException("diary not found"));
		}
		if (!diary.getUser().getId().equals(userService.getCurrentUser().getId())) {
			throw new ForbiddenException("no access as current user");
		}
		return diary;
	}
	
	@RequestMapping(value = "diary/{id}/{accessHash}", method = RequestMethod.GET)
	@ResponseBody
	public Diary getPuplicDiary(@PathVariable Long id, @PathVariable String accessHash, Locale locale, Model model) throws NotFoundException, ForbiddenException {

		Diary diary = diaryService.findFull(id);
		
		if (diary == null) {
			throw (new NotFoundException("diary not found"));
		}
		if (!diaryService.verifyAccessHash(diary.getId(), accessHash)) {
			throw new ForbiddenException("no access to diary");
		}
		return diary;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "diary/{id}/accesshash", method = RequestMethod.GET)
	@ResponseBody
	public String getDiaryAccessHash(@PathVariable Long id, Locale locale, Model model) throws NotFoundException, ForbiddenException {

		
		Diary diary = diaryService.find(id);
		
		if (diary == null) {
			throw (new NotFoundException("diary not found"));
		}
		if (!diary.getUser().getId().equals(userService.getCurrentUser().getId())) {
			throw new ForbiddenException("no access as current user");
		}
		
		return diaryService.getAccessHash(diary.getId());
	}
	
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "user/diary", method = RequestMethod.GET)
	@ResponseBody
	public List<Diary> getDiaryByCurrentUser(Locale locale, Model model) throws NotFoundException {

		List<Diary> l = diaryService.findByUser(userService.getCurrentUser());		
		return l;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "diary", method = RequestMethod.POST)
	@ResponseBody	
	public Object addDiary(@RequestBody Diary diary, BindingResult result, Locale locale, Model model) throws FieldErrorException {	

		diary.setUser(userService.getCurrentUser());
		diaryService.add(diary);		
		return diary;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "diary/{id}/update", method = RequestMethod.PUT)	
	@ResponseBody
	public Object updateDiary (@PathVariable Long id, HttpServletRequest request, Locale locale, Model model) throws JsonProcessingException, IOException, FieldErrorException, NotFoundException, ForbiddenException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		//merge json with object
		Diary diary = diaryService.find(id);
		
		if (diary == null) {
			throw new NotFoundException("diary not found");
		}
		if (!diary.getUser().getId().equals(userService.getCurrentUser().getId())) {
			throw new ForbiddenException("no access as current user");
		}
		
		Diary diaryToUpdate = objectMapper.readerForUpdating(diary).readValue(request.getReader());
	    
		//validate merged object
		BindingResult result = new BeanPropertyBindingResult(diaryToUpdate, diaryToUpdate.getClass().getName());
		
		defaultValidator.validate(diaryToUpdate, result);
		if (result.hasErrors()) {
			throw new FieldErrorException(result.getFieldErrors());
		}
		
		//update user
		diaryService.update(diaryToUpdate);
		
		Diary newDiary = diaryService.find(id);
		
		return newDiary;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "diary/{id}/delete", method = RequestMethod.DELETE)	
	@ResponseBody
	public Object deleteDiary (@PathVariable Long id, HttpServletRequest request, Locale locale, Model model) throws JsonProcessingException, IOException, FieldErrorException, NotFoundException, ForbiddenException {
		
		
		Diary diary = diaryService.find(id);
		
		if (diary == null) {
			throw new NotFoundException("diary not found");
		}
		if (!diary.getUser().getId().equals(userService.getCurrentUser().getId())) {
			throw new ForbiddenException("no access as current user");
		}
		
		diaryService.remove(diary);
		
		return true;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "diaryitem/{id}/delete", method = RequestMethod.DELETE)	
	@ResponseBody
	public Object deleteDiaryItem (@PathVariable Long id, HttpServletRequest request, Locale locale, Model model) throws JsonProcessingException, IOException, FieldErrorException, NotFoundException, ForbiddenException {
		
		
		DiaryItem diaryItem = diaryItemService.findFull(id);
		
		if (diaryItem == null) {
			throw new NotFoundException("diaryitem not found");
		}
		if (!diaryItem.getDiary().getUser().getId().equals(userService.getCurrentUser().getId())) {
			throw new ForbiddenException("no access as current user");
		}
		
		diaryItemService.remove(diaryItem);
		
		return true;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "diaryitem/{diaryId}", method = RequestMethod.POST)
	@ResponseBody	
	public Object addCustomDiaryItem(@PathVariable Long diaryId, @RequestBody DiaryItem diaryItem, BindingResult result, Locale locale, Model model) throws FieldErrorException, NotFoundException, ForbiddenException {	

		Diary diary = diaryService.find(diaryId);
		
		if (diary == null) {
			throw new NotFoundException("historyitem not found");
		}
		if (!diary.getUser().getId().equals(userService.getCurrentUser().getId())) {
			throw new ForbiddenException("no access as current user");
		}
		
		diaryItem.setDiary(diary);
		
		diaryItemService.add(diaryItem);		
		return diaryItem;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "diaryitem/{diaryId}/{historyItemId}", method = RequestMethod.POST)
	@ResponseBody	
	public Object addDiaryItem(@PathVariable Long diaryId, @PathVariable Long historyItemId, @RequestBody DiaryItem diaryItem, BindingResult result, Locale locale, Model model) throws FieldErrorException, NotFoundException, ForbiddenException {	
		
		
		Diary diary = diaryService.findFull(diaryId);
		HistoryItem historyItem = historyItemService.find(historyItemId);
		
		if (historyItem == null) {
			throw new NotFoundException("historyitem not found");
		}
		if (diary == null) {
			throw new NotFoundException("diary not found");
		}
		
		if (!diary.getUser().getId().equals(userService.getCurrentUser().getId())) {
			throw new ForbiddenException("no access as current user");
		}
		
		//check for duplicate history Item
		for (DiaryItem d : diary.getDiaryItems()) {			
			if (d.getHistoryItem() != null && d.getHistoryItem().getId().equals(historyItem.getId())) {
				throw new ForbiddenException("historyitem already in diary");
			}
		}
		
		diaryItem.setDiary(diary);
		diaryItem.setHistoryItem(historyItem);
		
		diaryItemService.add(diaryItem);		
		return diaryItem;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "diaryitem/{id}/update", method = RequestMethod.PUT)	
	@ResponseBody
	public Object updateDiaryItem (@PathVariable Long id, HttpServletRequest request, Locale locale, Model model) throws JsonProcessingException, IOException, FieldErrorException, NotFoundException, ForbiddenException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		//merge json with object
		DiaryItem diaryItem = diaryItemService.find(id);
		
		if (diaryItem == null) {
			throw new NotFoundException("diaryitem not found");
		}
		if (!diaryItem.getDiary().getUser().getId().equals(userService.getCurrentUser().getId())) {
			throw new ForbiddenException("no access as current user");
		}
		
		DiaryItem diaryItemToUpdate = objectMapper.readerForUpdating(diaryItem).readValue(request.getReader());
	    
		//validate merged object
		BindingResult result = new BeanPropertyBindingResult(diaryItemToUpdate, diaryItemToUpdate.getClass().getName());
		
		defaultValidator.validate(diaryItemToUpdate, result);
		if (result.hasErrors()) {
			throw new FieldErrorException(result.getFieldErrors());
		}
		
		//update user
		diaryItemService.update(diaryItemToUpdate);
		
		DiaryItem newDiaryItem = diaryItemService.find(id);
		
		return newDiaryItem;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value="diaryitem/{id}/file", method=RequestMethod.POST)
	@ResponseBody
	public Object uploadDiaryItemFile(@PathVariable Long id, @RequestBody String file, Locale locale, Model model) throws NotFoundException, IOException {

        byte[] imageByteArray = DatatypeConverter.parseBase64Binary(file);
		
        String folder = "resources/diaryitem_images";
        File f = fileService.uploadFile(imageByteArray, folder);
        
        if (f == null) {
        	throw new IOException("file could not be uploaded");
        }

		DiaryItem d = diaryItemService.find(id);
		if (d == null) {
			throw (new NotFoundException("diaryitem not found"));
		}
		
		f.setDiaryItem(d);
		fileService.add(f);
		
		userService.addPoints(userService.getCurrentUser(), new Long(1));
		
		return f;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "diaryitem/{diaryItemid}/file/{fileId}/delete", method = RequestMethod.DELETE)	
	@ResponseBody
	public Object deleteDiaryItemPicture (@PathVariable Long diaryItemid, @PathVariable Long fileId, HttpServletRequest request, Locale locale, Model model) throws JsonProcessingException, IOException, FieldErrorException, NotFoundException, ForbiddenException {
		
		
		DiaryItem diaryItem = diaryItemService.findFull(diaryItemid);
		
		if (diaryItem == null) {
			throw new NotFoundException("diaryitem not found");
		}
		if (!diaryItem.getDiary().getUser().getId().equals(userService.getCurrentUser().getId())) {
			throw new ForbiddenException("no access as current user");
		}
		
		File file = null;
		for (File f : diaryItem.getPictures()) {

			if (fileId.equals(f.getId())) {
				file = f;
				logger.info("found");
			}
		}
		
		if (file == null) {
			throw new NotFoundException("file not found");
		}
		
		fileService.remove(file);
		
		return true;
	}
	
}
