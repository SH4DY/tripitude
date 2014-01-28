package ac.tuwien.ase08.tripitude.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@Component
public class UserRegisterValidator implements Validator {
    
	@Autowired
	private IUserService userService;
	
	@Override
	public boolean supports(Class<?> clazz) {
	  return User.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		User user = (User) target;
		
		
		if (user.getPassword() != null && !user.getPassword().equals(user.getPasswordConfirmation())) {
		   errors.rejectValue("passwordConfirmation", "error.user.passwordConfirmation", "passwords do not match");
		}
		
		if (userService.getUserByEmail(user.getEmail()) != null) {
		   errors.rejectValue("email", "error.user.alreadyExists", "user already exists");
		}

	}

}
