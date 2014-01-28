package ac.tuwien.ase08.tripitude.controller.api;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javassist.NotFoundException;

import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;

import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.File;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.exception.FieldErrorException;
import ac.tuwien.ase08.tripitude.restauth.RestAuthLoginObj;
import ac.tuwien.ase08.tripitude.restauth.RestAuthNonceSessionObj;
import ac.tuwien.ase08.tripitude.restauth.RestAuthUtil;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventService;
import ac.tuwien.ase08.tripitude.service.interfaces.IFileService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;
import ac.tuwien.ase08.tripitude.validator.DefaultValidator;
import ac.tuwien.ase08.tripitude.validator.UserRegisterValidator;

@Controller
@RequestMapping("api")
public class UserRestController {

	@Autowired 
	private MessageSource messageSource;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IEventService eventService;
	
	@Autowired
	private IFileService fileService;
	
	@Autowired  
    private UserRegisterValidator userRegisterValidator;
	
	@Autowired
    private RestAuthUtil restAuthUtil;
	
	@Autowired
	private DefaultValidator defaultValidator;
	
	@Autowired
	ServletContext servletContext;
	
	private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "user/{id}", method = RequestMethod.GET)
	@ResponseBody
	public User get(@PathVariable Long id, Locale locale, Model model) throws NotFoundException {
		//logger.info("I'm the first controller");
		
		User u = userService.find(id);
		if (u != null) {
   		    u.setPassword(null);
   		    return u;
		}
		else {
			throw new NotFoundException("user not found");
		}
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "user", method = RequestMethod.GET)
	@ResponseBody
	public List<User> list(Locale locale, Model model) {
		//logger.info("I'm the first controller");
		List<User> l = userService.list();	
		//hide password in response
		for (User u : l) {
			u.setPassword(null);
		}
		return l;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "user/highscores", method = RequestMethod.GET)
	@ResponseBody
	public List<User> listHighscoreUser(Locale locale, Model model) {
		List<User> l = userService.highscoreList();	
		//hide password in response
		for (User u : l) {
			u.setPassword(null);
		}
		return l;
	}
	
	@RequestMapping(value = "user/me", method = RequestMethod.GET)
	@ResponseBody
	public User currentUser(Locale locale, Model model) throws NotFoundException {
		User u = userService.getCurrentUser();
		//hide password in response
		if (u != null) {
			u.setPassword(null);
			u.setPasswordConfirmation(null);
		}
		else {
			throw new NotFoundException("current user not found");
		}
		return u;
	}
	
	@RequestMapping(value = "user/logout", method = RequestMethod.GET)
	@ResponseBody
	public void logout(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model) {
		userService.logout(request, response);
	}

	@RequestMapping(value = "user/nonce", method = RequestMethod.GET)
	@ResponseBody
	public RestAuthNonceSessionObj getNonce(Locale locale, Model model) {
		String nonce = restAuthUtil.getNonce();
		//just for testing purposes
		logger.info("hashed_pass_nonce:" + restAuthUtil.generateHash(nonce + "cd3684ad70f0c1cb12213cd4a80889ffeb99ed57") + " nonce:" + nonce);
		RestAuthNonceSessionObj rns = new RestAuthNonceSessionObj();
		rns.setNonce(nonce);
		rns.setSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
		return rns;
	}
	
	@RequestMapping(value = "user/login", method = RequestMethod.POST)
	@ResponseBody
	public Object login(@RequestBody RestAuthLoginObj loginObj, Locale locale, Model model) throws AuthenticationException {
		logger.info("SUCCESS TRY");
		User u = userService.getCurrentUser();
		if (u != null) {
			return u;
		}
		u = userService.getUserByEmail(loginObj.getEmail());
		//check hashed nonce and password
		if (u != null && restAuthUtil.validateLoginRequest(loginObj)) {
			userService.autoLogin(u);
			u.setPassword("");
			u.setPasswordConfirmation("");
			logger.info("SUCCESS LOGIN");
			return u;	
		}
		else {
			throw new AuthenticationException("login failed");
		}
	}
		
	/**
	 * Register user
	 * @param user
	 * @param locale
	 * @param model
	 * @throws FieldErrorException 
	 */
	@RequestMapping(value = "user/register", method = RequestMethod.POST)
	@ResponseBody	
	public Object add(@Valid @RequestBody User user, BindingResult result, Locale locale, Model model) throws FieldErrorException {	
		//Custom validator
	    userRegisterValidator.validate(user, result);
		//check if validation failed
		if(result.hasErrors()) {
			throw new FieldErrorException(result.getFieldErrors());		
        }
		//add user
		userService.registerUser(user);
		user.setPassword("");
		user.setPasswordConfirmation("");
		return user;
	}
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "user/events", method = RequestMethod.GET)
	@ResponseBody
	public List<Event> getEvents(Locale locale, Model model){
		if(userService.getCurrentUser() != null){
			User u = userService.getCurrentUser();
			
			List<Event> userEvents = new ArrayList<Event>();
			
			for(Event e : eventService.list()){
				if(e.getUser().equals(u))
					userEvents.add(e);
			}
			
			userEvents.addAll(userService.getEvents(u.getId()));
			
			Collections.sort(userEvents, new Comparator<Event>(){
				@Override
				public int compare(Event e1, Event e2) {
					if (e1.getTime() == null || e2.getTime() == null)
						return 0;
					return e1.getTime().compareTo(e2.getTime());
				}
			});
			
			return userEvents;
		}
		return new ArrayList<Event>();
	}
	
	
	
	@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "avatar/save", method = RequestMethod.POST)
	@ResponseBody
	public File saveAvatar(@Valid @RequestBody String imageString, Locale locale, Model model)
			throws NotFoundException, FileNotFoundException {
		byte[] imageByteArray = decodeImage(imageString);
		User u = userService.getCurrentUser();
		MagicMatch match = null;		
		try {
			match = Magic.getMagicMatch(imageByteArray);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		String fileExtension = match.getExtension();
		String folder = "resources/avatar_images";
		String fullPath = servletContext.getRealPath("/" + folder);
		String filename =  "IMG_" + u.getName() + "." + fileExtension;
		String destination = fullPath + "/" + filename;
		// Write a image byte array into file system
		FileOutputStream imageOutFile;
		try {
			imageOutFile = new FileOutputStream(destination);
			imageOutFile.write(imageByteArray);

			imageOutFile.close();
		} catch (IOException e) {
			logger.error("Error reading image from file: IOException");
			e.printStackTrace();
		}
		File file = fileService.findByLocation(folder + "/" + filename);
		if(file == null){
			file = new File(folder + "/" + filename);
			fileService.add(file);
		}
		else{
			fileService.update(file);
		}
		u.setPicture(file);	
		userService.update(u);
		return file;
	}
	
	@RequestMapping(value = "avatar", method = RequestMethod.GET)
	@ResponseBody
	public String getAvatar(Locale locale, Model model) throws NotFoundException{
		String avatarPath = "";
		if (userService.getCurrentUser() == null) {
			logger.error("No user is logged in");
			throw (new NotFoundException("User not found"));
		}
		File avatar = userService.getCurrentUser().getPicture();
		java.io.File file = new java.io.File(avatar.getLocation());
		byte[] fileData = new byte[(int) file.length()];
		DataInputStream dis;
		try {
			dis = new DataInputStream(new FileInputStream(file));
			dis.readFully(fileData);
			dis.close();
		} catch (FileNotFoundException e) {
			logger.error("Error reading image from file: File not found");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error reading image from file: IOException");
			e.printStackTrace();
		}
		avatarPath = encodeImage(fileData);
		return avatarPath;
	}

	
	public static String encodeImage(byte[] imageByteArray) {
		return DatatypeConverter.printBase64Binary(imageByteArray);
	}

	public static byte[] decodeImage(String imageDataString) {
		return DatatypeConverter.parseBase64Binary(imageDataString);
	} 
	
	/* --------------------------------- NOT USED CURRENTLY! ----------------------------
	//@PreAuthorize("hasRole('AUTHENTICATED')")
	@RequestMapping(value = "user/{id}/update", method = RequestMethod.PUT)	
	@ResponseBody
	public Object update (@PathVariable Long id, HttpServletRequest request, Locale locale, Model model) throws JsonProcessingException, IOException, FieldErrorException, NotFoundException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		//merge json with object
		User user = userService.find(id);
		
		if (user == null) {
			throw new NotFoundException("user not found");
		}
		
		User userToUpdate = objectMapper.readerForUpdating(user).readValue(request.getReader());
	    
		//validate merged object
		BindingResult result = new BeanPropertyBindingResult(userToUpdate, userToUpdate.getClass().getName());
		
		defaultValidator.validate(userToUpdate, result);
		if (result.hasErrors()) {
			throw new FieldErrorException(result.getFieldErrors());
		}
		
		//update user
		userService.update(userToUpdate);
		
		User newUser = userService.find(id);
		
		user.setPassword("");
		user.setPasswordConfirmation("");
		
		return newUser;
	}*/
}
