package ac.tuwien.ase08.tripitude.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.UserService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;
import ac.tuwien.ase08.tripitude.validator.UserRegisterValidator;

/**
 * Handles requests for the application home page.
 */
@RequestMapping("user")
@Controller
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private IUserService userService;
	@Autowired 
	private IRoleService roleService;
	@Autowired  
    private UserRegisterValidator userRegisterValidator;

	
	
	@ModelAttribute("user")
    public User getRegisterForm() {
        return new User();
    }
	
	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String login(Locale locale, Model model, RedirectAttributes redirectAttributes) throws IOException {
		
		if (userService.getCurrentUser() == null) {			
			return "user/login";
		}
		else {	

			redirectAttributes.addFlashAttribute("warningMessage", "You are already logged in.");
			return "redirect:/";
		}
		
	}
	
	
	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String viewRegistration(Locale locale, Model model, RedirectAttributes redirectAttributes) throws IOException {
		
		if (userService.getCurrentUser() != null) {		
			
			redirectAttributes.addFlashAttribute("warningMessage", "You are already logged in.");
			return "redirect:/";
		}
		else {	
			//model.addAttribute("userForm", new User());
			return "user/register";
		}
		
	}
	
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public String processRegistration(@Valid User user, BindingResult result, Locale locale, Model model, RedirectAttributes redirectAttributes) throws IOException {
	
		//Custom validator
		userRegisterValidator.validate(user, result);
		
		if(result.hasErrors()) {
			//model.addAttribute("userForm", user);
			model.addAttribute("errorMessage", "Sorry, there are some incorrect fields.");
			user.setPassword("");
            return "user/register";
        }
		else 
		{				
			userService.registerUser(user);
			userService.autoLogin(user);
			redirectAttributes.addFlashAttribute("successMessage", "Thank you for your registration.");
			return "redirect:/";
		}
		
	}
	
	@RequestMapping(value="loginfailed", method = RequestMethod.GET)
	public String loginerror(Locale locale, Model model) {
 
		model.addAttribute("errorMessage", "Invalid login or password.");
		return "user/login";
 
	}
	
	@RequestMapping(value="loginsuccess", method = RequestMethod.GET)
	public String loginSuccess(Locale locale, Model model, RedirectAttributes redirectAttributes) {
		
		User u = userService.getCurrentUser();
		if (u != null) {			
			redirectAttributes.addFlashAttribute("successMessage", "Hi " + u.getName() + ". Nice to see you again!");
		}

		return "redirect:/";
 
	}
}
