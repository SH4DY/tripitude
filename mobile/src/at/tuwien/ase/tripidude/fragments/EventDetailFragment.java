package at.tuwien.ase.tripidude.fragments;

import java.util.Date;

import android.os.Bundle;
import android.view.View;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.UserAPI;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.Hotspot;
import at.tuwien.ase.tripidude.utils.Log;

public class EventDetailFragment extends FragmentController {
	
	private Event event;
	
	public static final String EVENT_ID_KEY = "eventIdKey";
	
	public static FragmentController newInstance(Long eventId){
		Bundle bundle = new Bundle();
		bundle.putLong(EVENT_ID_KEY, eventId);
		
		FragmentController mfragment = new EventDetailFragment();
		mfragment.setArguments(bundle);
		return mfragment;
	}
	
	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_event_detail;
	}

	@Override
	protected void onCreate() throws Exception {}

	@Override
	protected void onQuery() throws Exception {
		event = new Event("Eisessen", new Date(), "ashdkhaksj", new Hotspot("Eissalon", "dort kann man eis essen"), UserAPI.getCurrentUser());
	}

	@Override
	protected void onShow() throws Exception {
		
		Log.info(this, "Set Fields");
		if(event != null) {
			ui.id(R.id.event_name_field).text(event.getName());
			ui.id(R.id.event_location_field).text(event.getMapItem().getTitle());
			ui.id(R.id.event_time_field).text(event.getTime().toString());
			ui.id(R.id.category_field).text("Category: ");
			ui.id(R.id.description_field).text("Description:" + "blablabla....");
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
}
