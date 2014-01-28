package at.tuwien.ase.tripidude.fragments;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.MainActivity.OnActivityResultListener;
import at.tuwien.ase.tripidude.api.APIException;
import at.tuwien.ase.tripidude.api.TripitudeAPI;
import at.tuwien.ase.tripidude.api.UserAPI;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.fragments.UserProfileFragment.UserPropertySetter;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.User;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.MediaFileHandler;

public class UserProfileDescriptionFragment extends FragmentController implements UserPropertySetter, OnActivityResultListener {
	
	private User user;
	private static final int PICK_IMAGE_REQCODE = 1337;
	
	private Bitmap userAvatar;
	private SaveAvatar save;

	@Override
	protected int onDoCreateViewWithId() {		
		return R.layout.fragment_userprofile_description;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		((MainActivity) getActivity()).removeActivityResultListener(this);
	}
	
	@Override
	public void onDeactivate() {
		try {
			super.onDeactivate();
		} catch (Exception e) {
			Log.debug(this, "Could not deactivate");
		}
		((MainActivity) getActivity()).removeActivityResultListener(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		((MainActivity) getActivity()).removeActivityResultListener(this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		((MainActivity) getActivity()).addActivityResultListener(this);
	}

	@Override
	protected void onCreate() throws Exception {
		((MainActivity) getActivity()).addActivityResultListener(this);
	}

	@Override
	protected void onQuery() throws Exception {
	}

	@Override
	protected void onShow() throws Exception {
		ui.id(R.id.user_change_language_button).invisible();
		ui.id(R.id.user_change_language_title).invisible();
		ui.id(R.id.user_change_password).invisible();
		ui.id(R.id.user_password_display).invisible();
		if (user != null) {
			ui.id(R.id.user_name).text(user.getName());
			ui.id(R.id.user_email).text(user.getEmail());
			ui.id(R.id.user_rank).text(user.getRank() + " (" + user.getPoints() + ")");
			setAvatarImage(user);
		}
		
		ui.id(R.id.user_avatar_change_button).clicked(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				getActivity().startActivityForResult(i, PICK_IMAGE_REQCODE);
			}
		});
		
		ui.id(R.id.user_change_avatar).clicked(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				getActivity().startActivityForResult(i, PICK_IMAGE_REQCODE);
			}
		});
		
		ui.id(R.id.loading).visibility(View.GONE);
	}
	
	/**
	 * Set avatar image 
	 * @param user
	 * @throws Exception
	 */
	private void setAvatarImage(User user) throws Exception {
		if(user.getPicture() != null)
			ui.id(R.id.user_avatar).image(TripitudeAPI.getBaseUrlWeb() + user.getPicture().getLocation(), false, false);
		else
			ui.id(R.id.user_avatar).image(R.drawable.glyphicons_user);
	}	

	@Override
	protected View getLoadingView() {
		return null;
	}

	@Override
	protected View getRealView() {
		return null;
	}

	@Override
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public void setUserEvents(List<Event> events) {
	}

	@Override
	public void onActivityResultFromActivity(int requestCode, int resultCode,
			Intent data) {
		if (requestCode == PICK_IMAGE_REQCODE
				&& resultCode == Activity.RESULT_OK && data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getActivity().getContentResolver().query(
					selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			userAvatar = MediaFileHandler.decodeSampledBitmapFromUri(picturePath, 80, 80);
			save = new SaveAvatar();
			save.execute();
			ui.id(R.id.user_avatar).image(userAvatar);
		} else if((requestCode == PICK_IMAGE_REQCODE
				&& resultCode == Activity.RESULT_CANCELED) || (requestCode == PICK_IMAGE_REQCODE && data != null)){
			Toast.makeText(getActivity(), "Image could not be selected",
					Toast.LENGTH_LONG).show();
		}
	}
	
	
	private class SaveAvatar extends
	android.os.AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {	
			boolean imagesSaved;	
			try {
				imagesSaved = UserAPI.getInstance().saveAvatar(userAvatar);
			} catch (APIException e) {
				Log.debug(UserProfileDescriptionFragment.this, "Could not save avatar");
				return false;
			}
			return imagesSaved;
		}
	}
}
