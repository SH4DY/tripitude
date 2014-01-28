package at.tuwien.ase.tripidude.validator;

import android.widget.EditText;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.core.App;

public class UserRegisterValidator {
	
	private EditText username;
	private EditText email;
	private EditText password;
	private EditText passwordConfirm;
	
	private final int userNameMin = 6;
	private final int userNameMax = 30;
	
	private final int passwordMin = 6;
	private final int passwordMax = 30;
	
	public UserRegisterValidator(EditText username, 
								 EditText email, 
								 EditText password, 
								 EditText passwordConfirm) {
		
		this.username = username;
		this.email = email;
		this.password = password;
		this.passwordConfirm = passwordConfirm;
	}
	
	public Boolean validate() {
		
		Boolean success = true;
		
		if (username.getText().toString().length() < userNameMin || username.getText().toString().length() > userNameMax) {
			
			String error = App.resources.getString(R.string.error_string_length_between, userNameMin, userNameMax);
			username.setError(error);
			success = false;
		}
		
		if (!password.getText().toString().equals(passwordConfirm.getText().toString())) {
			
			String error = App.resources.getString(R.string.error_passwords_do_not_match);
			passwordConfirm.setError(error);
			success = false;
		}
		
		
		if (password.getText().toString().length() < passwordMin || password.getText().toString().length() > passwordMax) {
			
			String error = App.resources.getString(R.string.error_string_length_between, passwordMin, passwordMax);
			password.setError(error);
			success = false;
		}
		
		if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
			String error = App.resources.getString(R.string.error_email_not_valid);
			email.setError(error);
			success = false;
		}
		
		return success;
	}

}
