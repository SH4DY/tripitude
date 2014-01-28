package at.tuwien.ase.tripidude.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.UserAPI;
import at.tuwien.ase.tripidude.core.FragmentController;

public class LoginFragment extends FragmentController {

	private OnLoginFragmentListener mCallback;

	public interface OnLoginFragmentListener {
		public void onLoginSuccess();
		public void onGoToRegistration();
	}

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_login;
	}

	@Override
	protected void onCreate() throws Exception {
		// Login on click listener
		ui.id(R.id.login_button).getButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String email = ui.id(R.id.login_email_field).getText().toString(); 
				String password = ui.id(R.id.login_password_field).getText().toString();

				ManualUserLoginOperation op = new ManualUserLoginOperation();
				op.execute(new String[] {email, password});
			}
		});

		// Go to registration listener
		ui.id(R.id.go_to_registration).getTextView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onGoToRegistration();
			}
		});

	}
	
	@Override
	protected boolean isQueryNeeded() {
		return false;
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
			mCallback = (OnLoginFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnLoginFragmentListener");
		}
	}

	private class ManualUserLoginOperation extends AsyncTask<String, Void, String>  {

		@Override
		protected String doInBackground(String... params) {
			UserAPI.getInstance().doCheckUserSession();
			String success = Boolean.toString(UserAPI.getInstance().doManualLogin(params[0], params[1]));			
			return success;
		}

		@Override
		protected void onPostExecute(String result) {

			if (Boolean.valueOf(result)) {	
				//hide keyboard
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromInputMethod(getActivity().getCurrentFocus().getWindowToken(), 0);
				mCallback.onLoginSuccess();
			} else {
				ui.id(R.id.login_status).text(getResources().getString(R.string.login_failed));
			}
		}
	}
}
