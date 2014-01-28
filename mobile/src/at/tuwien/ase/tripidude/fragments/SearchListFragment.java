package at.tuwien.ase.tripidude.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.MapItemAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.ModelsAdapter;
import at.tuwien.ase.tripidude.utils.Utils;

public class SearchListFragment extends FragmentController {
	
	private static final String TYPE = "type";
	private static final String NAME = "name";
	private static final String CATEGORY = "category";
	private static final String BOUNDINGCIRCLE = "boundingCircle";
	private static final String EVENTSEARCH = "eventSearch";
	private static final String BEGINDATE = "beginDate";
	private static final String ENDDATE = "endDate";
	
	private String type;
	private String name;
	private String category;
	private double[] boundingCircle;
	private boolean events;
	private Date beginDate;
	private Date endDate;
	
	private List<MapItem> entryList;
	private ModelsAdapter<MapItem> mapItemsAdapter;
		
	public static FragmentController newInstance(String type, String name, String category, Double[] boundingCircle, boolean events, Date _beginDate, Date _endDate) {
		FragmentController list = new SearchListFragment();
		list.setUseLoadingBackground(false);
		Bundle args = new Bundle();
		args.putString(TYPE, type);
		args.putString(NAME, name);
		args.putString(CATEGORY, category);
		args.putDoubleArray(BOUNDINGCIRCLE, Utils.getPrimitive(boundingCircle));
		args.putBoolean(EVENTSEARCH, events);

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
		args.putString(BEGINDATE, format.format(_beginDate));
		args.putString(ENDDATE, format.format(_endDate));
		
		list.setArguments(args);
		return list;
	}

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_mapitem_search_list;
	}

	@Override
	protected void onCreate() throws Exception {
		type = getArguments() != null ? getArguments().getString(TYPE) : "";
		name = getArguments() != null ? getArguments().getString(NAME) : "";
		category = getArguments() != null ? getArguments().getString(CATEGORY) : "";
		boundingCircle = getArguments() != null ? getArguments().getDoubleArray(BOUNDINGCIRCLE) : null;
		events = getArguments() != null ? getArguments().getBoolean(EVENTSEARCH) : false;
		
		String beginDateString = getArguments() != null ? getArguments().getString(BEGINDATE) : "";
		String endDateString = getArguments() != null ? getArguments().getString(ENDDATE) : "";
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
		beginDate = new Date();
		endDate = new Date();
		try {
			if (!Utils.isNullOrEmpty(beginDateString))
				beginDate = format.parse(beginDateString);
			if (!Utils.isNullOrEmpty(endDateString))
				endDate = format.parse(endDateString);
		} catch (ParseException e) {
			
		}
		
		// OnItemClickListener
		ui.id(R.id.search_list).getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> aView, View view, int location, long arg3) {
				onListItemClick(entryList.get(location));
			}
		});
	}
	
	protected void onListItemClick(MapItem item) {
		App.mapListener.showMapItem(item);
		getActivity().onBackPressed();
	}
	
	@Override
	protected void onQuery() throws Exception {
		if(events) {
			Log.debug(SearchListFragment.this, "Searching for Mapitems with Events between "+beginDate.toString()+" and "+endDate.toString() +
					" and with parameters: "+ type+", "+ name+", "+category+", "+getBoundingString(boundingCircle));
			entryList = MapItemAPI.getInstance().getHotspotsWithEvents(type, name, category, getBoundingString(boundingCircle), beginDate, endDate);
		}
		else {
			Log.debug(SearchListFragment.this, "Searching for Mapitems with parameters: "+ type+", "+ name+", "+category+", "+getBoundingString(boundingCircle));
			entryList = MapItemAPI.getInstance().getHotspotsExtended(type, name, category, getBoundingString(boundingCircle));
		}
	}

	@Override
	protected void onShow() throws Exception {

		if(Utils.isNullOrEmpty(entryList)) {
			Toast.makeText(getActivity(), "No Results found", Toast.LENGTH_LONG).show();
			getActivity().onBackPressed();
		}
		
		// fill adapter
		mapItemsAdapter = new ModelsAdapter<MapItem>(getActivity(), R.layout.list_item_mapitem_search, entryList) {
			@Override
			protected void fillView(int position, MapItem model, View view, UiHolder ui) {
				// title
				ui.id(R.id.mapitem_title).text(model.getTitle());	
				// description
				ui.id(R.id.mapitem_desc).textOrGone(model.getDescription());	
				// category
				if(!Utils.isNullOrEmpty(model.getCategories()))
					ui.id(R.id.mapitem_category).textOrGone(model.getCategories().get(0).getName());
			}
		};
		// set adapter
		ui.id(R.id.search_list).adapter(mapItemsAdapter);
	}

	@Override
	protected View getLoadingView() {
		return ui.id(R.id.loading).getView();
	}

	@Override
	protected View getRealView() {
		return ui.id(R.id.search_list).getView();
	}
	
	private String getBoundingString(double[] boundingCircle) {
		return String.valueOf(boundingCircle[0])+","+String.valueOf(boundingCircle[1])+","+String.valueOf(boundingCircle[2]);
	}
}
