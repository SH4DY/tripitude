package ac.tuwien.ase08.tripitude.exception;

import java.util.List;

import org.springframework.validation.FieldError;

public class FieldErrorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<FieldError> errors;


	public FieldErrorException() {
		super();
	}

	public FieldErrorException(String err) {
		super(err);
	}
	
	public FieldErrorException(List<FieldError> errors) {
		super();
		this.errors = errors;
	}
	
	public List<FieldError> getErrors() {
		return errors;
	}
	
	public void setErrors(List<FieldError> errors) {
		this.errors = errors;
	}
}
