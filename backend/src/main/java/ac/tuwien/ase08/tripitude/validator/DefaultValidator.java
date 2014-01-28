package ac.tuwien.ase08.tripitude.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class DefaultValidator implements Validator{

	private ValidatorFactory factory;

	public DefaultValidator() {
		this.factory = Validation.buildDefaultValidatorFactory();
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		
		Set<ConstraintViolation<Object>> violations = factory.getValidator().validate(target);
	
		for (ConstraintViolation<Object> violation : violations) {
			errors.rejectValue(violation.getPropertyPath().toString(), "",  violation.getMessage());
		}
	}
}
