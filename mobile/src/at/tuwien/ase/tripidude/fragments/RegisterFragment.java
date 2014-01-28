package at.tuwien.ase.tripidude.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.APIException;
import at.tuwien.ase.tripidude.api.UserAPI;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.validator.UserRegisterValidator;

public class RegisterFragment extends FragmentController {
	
    private OnRegisterFragmentListener mCallback;
    private UserRegisterValidator userRegisterValidator;

	
	public interface OnRegisterFragmentListener {
		public void onRegisterSuccess();
	}

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_register;
	}

	@Override
	protected void onCreate() throws Exception {
		
		userRegisterValidator = new UserRegisterValidator(ui.id(R.id.register_user_name_field).getEditText(), 
				  ui.id(R.id.register_email_field).getEditText(), 
				  ui.id(R.id.register_password_field).getEditText(), 
				  ui.id(R.id.register_password_confirmation_field).getEditText());
		
		ui.id(R.id.register_button).getButton().setOnClickListener(new View.OnClickListener() {
					
			@Override
			public void onClick(View v) {
				
                //validate and set errors
                if (!userRegisterValidator.validate()) {                	
                	return;
                }
                
                UserRegisterOperation op = new UserRegisterOperation();
                op.execute(new String[] {ui.id(R.id.register_user_name_field).getText().toString(), 
                		                 ui.id(R.id.register_email_field).getText().toString(), 
                		                 ui.id(R.id.register_password_field).getText().toString()});
			}
		});

	}

	@Override
	protected void onQuery() throws Exception {}

	@Override
	protected void onShow() throws Exception {}

	@Override
	protected View getLoadingView() {
		return null;
	}

	@Override
	protected View getRealView() {
		return null;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnRegisterFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRegisterFragmentListener");
        }
	}
	
	private class UserRegisterOperation extends AsyncTask<String, Void, Boolean>  {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				return UserAPI.getInstance().doRegisterUser(params[0], params[1], params[2]);
			} catch (APIException e) {
				return false;
			}			
		}
		
		@Override
	    protected void onPostExecute(Boolean result) {
			if (result) {				
				//hide keyboard
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromInputMethod(getActivity().getCurrentFocus().getWindowToken(), 0);
		    	
				mCallback.onRegisterSuccess();
			}
			else {
				ui.id(R.id.register_status).text(getResources().getString(R.string.login_failed));
			}
	    }	
	}
}
