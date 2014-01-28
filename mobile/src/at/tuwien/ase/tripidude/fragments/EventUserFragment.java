package at.tuwien.ase.tripidude.fragments;

import java.util.List;

import android.view.View;
import android.widget.LinearLayout;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.APIException;
import at.tuwien.ase.tripidude.api.EventAPI;
import at.tuwien.ase.tripidude.api.TripitudeAPI;
import at.tuwien.ase.tripidude.api.UserAPI;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.fragments.EventFragment.EventPropertySetter;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.User;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.UiQuery;
import at.tuwien.ase.tripidude.utils.Utils;

public class EventUserFragment extends FragmentController implements
		EventPropertySetter {

	private Event event;
	private List<User> attendingUsers = null;
	private AttendEvent attend;
	private UnattendEvent unattend;
	private boolean attending = false;

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_event_users;
	}

	@Override
	protected void onCreate() throws Exception {
		
		ui.id(R.id.button_attend_event).clicked(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(attending){
					unattend = new UnattendEvent();
					unattend.execute();
					attendingUsers.remove(UserAPI.getCurrentUser());
					attending = false;
				}
				else{
					attend = new AttendEvent();
					attend.execute();
					attendingUsers.add(UserAPI.getCurrentUser());
					attending = true;
				}
				refreshView();
			}
		});
	}

	@Override
	protected void onQuery() throws Exception {}

	@Override
	protected void onShow() throws Exception {		
		Log.info(this, event.getUser().getName() + "; " + UserAPI.getCurrentUser().getName());
		// hide attend button if user isn't logged in or host of the event
		if (UserAPI.getCurrentUser() == null || event.getUser().getName().equals(UserAPI.getCurrentUser().getName())) {
			ui.id(R.id.button_attend_event).visibility(View.INVISIBLE);
		}
		if (event != null && !Utils.isNullOrEmpty(attendingUsers)) {
			initFields();
		}
	}
	
	/**
	 * Init content fields
	 */
	private void initFields() {
		if (Utils.isNullOrEmpty(attendingUsers))
			return;

		LinearLayout usersList = (LinearLayout) ui.id(R.id.event_user_list)
				.getView();

		if (usersList != null && usersList.getChildCount() > 0) {
			usersList.removeAllViews();
		}
		
		if(attendingUsers.contains(UserAPI.getCurrentUser())){
			attending = true;
			ui.id(R.id.button_attend_event).text("Unattend Event");
		}
		else{
			ui.id(R.id.button_attend_event).text("Attend Event");
		}
		
		for (final User u : attendingUsers) {
			UiQuery uiq = new UiQuery(getActivity().getLayoutInflater()
					.inflate(R.layout.list_item_event_user, null));
			if(u.getPicture() != null)
				uiq.id(R.id.user_avatar).image(TripitudeAPI.getBaseUrlWeb() + u.getPicture().getLocation(), false, false);
			else
				uiq.id(R.id.user_avatar).image(R.drawable.glyphicons_user);
			uiq.id(R.id.attending_user).text(u.getName());
			uiq.id(R.id.user_email).text(u.getEmail());
			uiq.id(R.id.user_rank).text(u.getRank());
			usersList.addView(uiq.getRootView());
		}
	}
	
	private void refreshView(){
		if(attendingUsers.contains(UserAPI.getCurrentUser())){
			attending = true;
			ui.id(R.id.button_attend_event).text("Unattend Event");
		}
		else{
			ui.id(R.id.button_attend_event).text("Attend Event");
		}

		LinearLayout usersList = (LinearLayout) ui.id(R.id.event_user_list)
				.getView();

		if (usersList != null && usersList.getChildCount() > 0) {
			usersList.removeAllViews();
		}
		
		if (Utils.isNullOrEmpty(attendingUsers))
			return;
		
		for (final User u : attendingUsers) {
			UiQuery uiq = new UiQuery(getActivity().getLayoutInflater()
					.inflate(R.layout.list_item_event_user, null));
			if(u.getPicture() != null)
				uiq.id(R.id.user_avatar).image(TripitudeAPI.getBaseUrlWeb() + u.getPicture().getLocation(), false, false);
			else
				uiq.id(R.id.user_avatar).image(R.drawable.glyphicons_user);
			uiq.id(R.id.attending_user).text(u.getName());
			uiq.id(R.id.user_email).text(u.getEmail());
			uiq.id(R.id.user_rank).text(u.getRank());
			usersList.addView(uiq.getRootView());
		}
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
	public void setEvent(Event event) {
		this.event = event;
	}

	@Override
	public void setAttendingUsers(List<User> attendingUsers) {
		this.attendingUsers = attendingUsers;
	}

	private class AttendEvent extends
			android.os.AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				return EventAPI.getInstance().attendEvent(event.getId());
			} catch (APIException e) {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}

	private class UnattendEvent extends
			android.os.AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				return EventAPI.getInstance().unattendEvent(event.getId());
			} catch (APIException e) {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}
}
