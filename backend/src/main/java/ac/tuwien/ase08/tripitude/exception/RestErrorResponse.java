package ac.tuwien.ase08.tripitude.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Get the users details for the 'personal' page
 */
public class RestErrorResponse {
	
	int status;
	
	String message;
	
	List<String> fieldErrors;
	
	public RestErrorResponse(int status, String message, List<String> fieldErrors) {
		
		this.status = status;
		this.message = message;
		this.fieldErrors = fieldErrors;
	}
	
    public RestErrorResponse(int status, String message) {
		
		this.status = status;
		this.message = message;
		this.fieldErrors = new ArrayList<String>();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getFieldErrors() {
		return fieldErrors;
	}

	public void setFieldErrors(List<String> fieldErrors) {
		this.fieldErrors = fieldErrors;
	}
	
	public void addFieldError(String fieldError) {
		this.fieldErrors.add(fieldError);
	}
	
	
}