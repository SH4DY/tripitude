package at.tuwien.ase.tripidude.fragments;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.view.View;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.fragments.EventFragment.EventPropertySetter;
import at.tuwien.ase.tripidude.models.Coordinate;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.User;
import at.tuwien.ase.tripidude.models.utils.EventCategoryUtils;

public class EventDescriptionFragment extends FragmentController implements EventPropertySetter {
	
	private Event event;
	private Boolean staticMapLoaded = false;
	
	@Override
	protected int onDoCreateViewWithId() {		
		return R.layout.fragment_event_description;
	}

	@Override
	protected void onCreate() throws Exception {
	}

	@Override
	protected void onQuery() throws Exception {
	}

	@Override
	protected void onShow() throws Exception {
		if (event != null) {
			initFields();
			if (!staticMapLoaded) {
				setStaticMapImage(event.getMapItem().getCoordinate());
				staticMapLoaded = true;
			}
		}
		ui.id(R.id.loading).visibility(View.GONE);
	}
	
	/**
	 * Init content fields
	 */
	private void initFields() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm", Locale.getDefault());
		
		ui.id(R.id.event_title).text(event.getName());
		ui.id(R.id.event_location).text(event.getMapItem().getTitle());
		ui.id(R.id.event_category).text(EventCategoryUtils.getStringListForEventCategoryList(event.getCategories()));
		ui.id(R.id.event_description).text(event.getDescription()); 
		ui.id(R.id.event_time).text(sdf.format(event.getTime()));
		ui.id(R.id.event_host).text(event.getUser().getName());
	}	
	
	/**
	 * Set static image 
	 * @param coord
	 * @throws Exception
	 */
	private void setStaticMapImage(Coordinate coord) throws Exception {	
		String url = "http://maps.googleapis.com/maps/api/staticmap?markers=";
		url += coord.getLatitude().toString() + ","+ coord.getLongitude().toString();
		url += "&zoom=17&size=180x180&sensor=false";
		
		ui.id(R.id.static_map_image).image(url);
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
	}	
}
