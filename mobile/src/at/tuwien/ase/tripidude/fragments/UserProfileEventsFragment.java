package at.tuwien.ase.tripidude.fragments;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.UserAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.fragments.UserProfileFragment.UserPropertySetter;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.User;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.UiQuery;
import at.tuwien.ase.tripidude.utils.Utils;

public class UserProfileEventsFragment extends FragmentController implements UserPropertySetter {

	User user;
	private List<Event> events = null; 
	
	@Override
	protected int onDoCreateViewWithId() {		
		return R.layout.fragment_userprofile_events;
	}

	@Override
	protected void onCreate() throws Exception {
		Spinner spinner = (Spinner) getView().findViewById(
				R.id.event_time_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.event_period_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> aView, View view,
					int pos, long id) {
				Log.info(UserProfileEventsFragment.this, "Item selected " + pos);
				initFields(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> aView) {
				//do Nothing.
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
	protected void onShow() throws Exception {
		if (user != null && !Utils.isNullOrEmpty(events)) {
			initFields(0);
		}
	}
	
	/**
	 * Init content fields
	 */
	private void initFields(int mode) {
		if (Utils.isNullOrEmpty(events))
			return;
		
		LinearLayout eventsList = (LinearLayout) ui.id(R.id.user_event_list).getView();
		
		if (eventsList != null && eventsList.getChildCount() > 0) {	
			eventsList.removeAllViews();
		}
		
		for (final Event e : events) {
			//all events
			if(mode == 0){
				initEvent(eventsList, e);
			}
			//events next week
			else if(mode == 1){
				if(!e.getTime().before(new Date()) && !e.getTime().after(addDaysToDate(new Date(), 7))){
					initEvent(eventsList, e);
				}
			}
			//events next 30 days
			else if(mode == 2){
				if(!e.getTime().before(new Date()) && !e.getTime().after(addDaysToDate(new Date(), 30))){
					initEvent(eventsList, e);
				}
			}
			//past events
			else{
				if(!e.getTime().after(addDaysToDate(new Date(), -1))){
					initEvent(eventsList, e);
				}
			}
		}	
	}
	
	private void initEvent(LinearLayout eventsList, Event e){
		final Event event = e;
		UiQuery uiq = new UiQuery(getActivity().getLayoutInflater().inflate(R.layout.list_item_event_userprofile, null));
		uiq.id(R.id.event_name).text(e.getName());
		if(e.getUser().equals(UserAPI.getCurrentUser()))
			uiq.id(R.id.event_location_host).text("Location: " + e.getMapItem().getTitle() + ", Host: You");
		else
			uiq.id(R.id.event_location_host).text("Location: " + e.getMapItem().getTitle() + ", Host: " + e.getUser().getName());
		uiq.id(R.id.event_time).text(e.getTime().toString());
		uiq.id(R.id.event_container).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putLong(EventFragment.KEY_EVENT_ID, event.getId());
				EventFragment fragment = new EventFragment();
				fragment.setArguments(args);
				((MainActivity) App.activity).addFragmentController(fragment, 1);
			}
		});
		eventsList.addView(uiq.getRootView());
	}
	
	private Date addDaysToDate(final Date date, int noOfDays) {
	    Date newDate = new Date(date.getTime());

	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(newDate);
	    calendar.add(Calendar.DATE, noOfDays);
	    newDate.setTime(calendar.getTime().getTime());

	    return newDate;
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
		this.events = events;
	}
}
