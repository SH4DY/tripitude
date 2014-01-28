package at.tuwien.ase.tripidude.api;

import java.io.IOException;

public class APIException extends IOException {

	private static final long serialVersionUID = 1L;

	public APIException() {
		super();
	}

	public APIException(String err) {
		super(err);
	}
	
	public APIException(RestErrorResponse error) {
		super();
		StringBuilder sb = new StringBuilder();
		sb.append("Status: " + error.getStatus() + "\n");
		sb.append("DefaultMessage: " + error.message + "\n");
		for (String fieldError : error.getFieldErrors()) {
			sb.append("FieldErrors: " + fieldError + "\n");
		}
		this.initCause(new Throwable(sb.toString()));
	}
}
