package at.tuwien.ase.tripidude.fragments;

import java.text.SimpleDateFormat;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.MapItemAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.fragments.MapItemFragment.MapItemPropertySetter;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.utils.UiQuery;
import at.tuwien.ase.tripidude.utils.Utils;

public class MapItemEventsFragment extends FragmentController implements MapItemPropertySetter {

	private MapItem mapItem;
	private List<Event> events = null; 
	private boolean returnFromCreate = false;
	
	@Override
	protected int onDoCreateViewWithId() {		
		return R.layout.fragment_mapitem_events;
	}

	@Override
	protected void onCreate() throws Exception {
		ui.id(R.id.button_create_event).clicked(new View.OnClickListener() {
			public void onClick(View v) {
				if (mapItem == null)
					return;
				FragmentController createEventFragment = new CreateEventFragment();
				((MainActivity) getActivity()).addFragmentController(createEventFragment);
				returnFromCreate = true;
			}
		});
	}

	@Override
	protected void onQuery() throws Exception {	
		if(returnFromCreate){
			setMapItemEvents(null);
			List<Event> _events = MapItemAPI.getInstance().getEvents(((MainActivity) getActivity()).getCurrentMapItem().getId());
			setMapItemEvents(_events);
			returnFromCreate = false;
		}
	}

	@Override
	protected void onShow() throws Exception {
		if (mapItem != null && !Utils.isNullOrEmpty(events)) {
			initFields();
		}
	}
	
	/**
	 * Init content fields
	 */
	private void initFields() {
		if (Utils.isNullOrEmpty(events))
			return;
		
		LinearLayout eventsList = (LinearLayout) ui.id(R.id.mapitem_event_list).getView();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
		
		if (eventsList != null && eventsList.getChildCount() > 0) {			
			eventsList.removeAllViews();
		}

		for (final Event e : events) { 
			UiQuery uiq = new UiQuery(getActivity().getLayoutInflater().inflate(R.layout.list_item_event, null));
			uiq.id(R.id.event_name).text(e.getName());
			uiq.id(R.id.event_time).text(sdf.format(e.getTime()));
			
			uiq.id(R.id.event_container).clicked(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle args = new Bundle();
					args.putLong(EventFragment.KEY_EVENT_ID, e.getId());
					EventFragment fragment = new EventFragment();
					fragment.setArguments(args);
					((MainActivity) App.activity).addFragmentController(fragment, 1);
				}
			});
			eventsList.addView(uiq.getRootView());
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
	public void setMapItem(MapItem mapItem) {
		this.mapItem = mapItem;
		
	}

	@Override
	public void setMapItemEvents(List<Event> events) {
		this.events = events;
	}
}
