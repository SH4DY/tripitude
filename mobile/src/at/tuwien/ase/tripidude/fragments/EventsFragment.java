package at.tuwien.ase.tripidude.fragments;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.UserAPI;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.models.User;
import at.tuwien.ase.tripidude.utils.Log;

public class EventsFragment extends FragmentController {
	
	private static final String COORD_KEY = "coord_key";
	
	private JSONObject test;
	private SaveForm save;
		
	public static FragmentController newInstance(String coords) {
		Bundle bundle = new Bundle();
		bundle.putString(COORD_KEY, coords);
		
		FragmentController events = new EventsFragment();
		events.setArguments(bundle);
		return events;
	}

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_events;
	}

	@Override
	protected void onCreate() throws Exception {
		ui.id(R.id.sample_text).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save = new SaveForm();
				save.execute();
				Toast.makeText(getActivity(), "Test", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onQuery() throws Exception {}

	@Override
	protected void onShow() throws Exception {
		if (test != null)
			Log.info(this, test.toString());
		
		User u = UserAPI.getCurrentUser();
		
		String t = "Not logged in";
		if (u != null) {
			t = u.getName();
		}
		
		ui.id(R.id.sample_text).text("Hallo User:" + t);
		ui.id(R.id.sample_image).image("http://4.bp.blogspot.com/-FmPwuIqTzqY/UdVd62I0wkI/AAAAAAAANHI/m_FlKF6ibeU/s640/Android+wallpapers+for+mobiles.jpg");
	}

	@Override
	protected View getLoadingView() {
		return null;
	}

	@Override
	protected View getRealView() {
		return null;
	} 
	
	private class SaveForm extends android.os.AsyncTask<Integer, Integer, JSONObject> {

		@Override
		protected JSONObject doInBackground(Integer... params) {
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			// UIThread
			ui.id(R.id.sample_text).text(result.toString());
			super.onPostExecute(result);
		}
		
	}
}
