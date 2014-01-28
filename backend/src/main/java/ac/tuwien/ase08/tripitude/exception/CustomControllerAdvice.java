package ac.tuwien.ase08.tripitude.exception;

import java.io.IOException;
import java.util.Iterator;

import javassist.NotFoundException;

import javax.security.sasl.AuthenticationException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomControllerAdvice {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomControllerAdvice.class);

	/**
	 * Catch IOException and redirect to a 'personal' page.
	 */
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {

		return new ResponseEntity<Object>(new RestErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
	}
	
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {

		Iterator<ConstraintViolation<?>> iter = ex.getConstraintViolations().iterator();
		
		RestErrorResponse response = new RestErrorResponse(HttpStatus.BAD_REQUEST.value(), "");
		
		while (iter.hasNext()) {
			
			ConstraintViolation<?> violation = iter.next();
			response.addFieldError(violation.getPropertyPath() + ": " + violation.getMessage());
		}
				
		return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(FieldErrorException.class)
	public ResponseEntity<Object> handleFieldErrorException(FieldErrorException ex) {
		
		RestErrorResponse response = new RestErrorResponse(HttpStatus.BAD_REQUEST.value(), "");
		
		for (FieldError error : ex.getErrors()) {
			response.addFieldError(error.getField() + ": " + error.getDefaultMessage());
		}
		
		return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(AuthenticationException.class) 
	public ResponseEntity<Object> handleAuthenticationExceptionException(AuthenticationException ex) {

		return new ResponseEntity<Object>(new RestErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()), HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(ForbiddenException.class) 
	public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex) {

		return new ResponseEntity<Object>(new RestErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage()), HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(IOException.class) 
	public ResponseEntity<Object> handleIOException(IOException ex) {

		return new ResponseEntity<Object>(new RestErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
	}
	

}