package at.tuwien.ase.tripidude.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.APIException;
import at.tuwien.ase.tripidude.api.MapItemAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.maputils.PositionUpdateListener;
import at.tuwien.ase.tripidude.models.Coordinate;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.utils.Log;

public class SearchMapitemFragment extends FragmentController implements PositionUpdateListener {

	private static final String TYPE_ALL = "all";
	private static final String TYPE_ROUTE = "route";
	private static final String TYPE_HOTSPOT = "hotspot";
	private static final int BEGIN_TIME = 1;
	private static final int END_TIME = 2;

	private boolean eventSearch;
	
	private int begin_day, begin_month, begin_year, end_day, end_month, end_year;
		
	private SearchForm search;
	private List<CharSequence> dbCategoryList;
	private String type;
	

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_mapitem_search;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		App.mapListener.removeOnPositionUpdateListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		App.mapListener.addOnPositionUpdateListener(this);
	}

	@Override
	protected void onCreate() throws Exception {

		//Set initial Type for Search
		this.setTypeAll();
		ui.id(R.id.button_all).activated(true);
		setEventSearch(false);
		ui.id(R.id.button_event).activated(getEventSearch());


		//Fill Category Spinner
		CharSequence[] itemArray = getResources().getTextArray(R.array.category_array);
		List<CharSequence> itemList = new ArrayList<CharSequence>(Arrays.asList(itemArray));
		itemList.add(0, MapItemAPI.CATEGORIES_ALL);

		CharSequence[] itemdbArray = getResources().getTextArray(R.array.category_id_array);
		dbCategoryList = new ArrayList<CharSequence>(Arrays.asList(itemdbArray));

		Log.info(this, "list: "+dbCategoryList);
		dbCategoryList.add(0, getString(R.string.categories_all));

		Spinner spinner = (Spinner) getView().findViewById(R.id.category_spinner);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), 
				android.R.layout.simple_spinner_item, itemList);


		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		ui.id(R.id.name_field).getEditText().setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.debug(SearchMapitemFragment.this, "inside!!!!!");
				if((event.getAction() == KeyEvent.ACTION_DOWN) &&
			            (keyCode == KeyEvent.KEYCODE_ENTER)) {
					Log.debug(SearchMapitemFragment.this, "enter!!!!!");
					setMapItemSearchParams();
					executeSearch(false);
					Toast.makeText(getActivity(), "Searching...", Toast.LENGTH_SHORT).show();
					App.mapListener.removeOnPositionUpdateListener(SearchMapitemFragment.this);
					return true;
				}
				return false;
			}
		});

		//set event search times
		Calendar c = Calendar.getInstance();
		// apparently month ranges 0-11, so add 1
		setTime(BEGIN_TIME, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
		c.add(Calendar.DATE, 7);
		// apparently month ranges 0-11, so add 1
		setTime(END_TIME, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));		
		
		//set default search parameter
		setMapItemSearchParams();
		App.mapListener.setSearchForMapItems(true);
		
		// load map with default parameters
		App.mapListener.searchForNewMapItems();

		//Add OnClickListener to buttons
		ui.id(R.id.button_search).clicked(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setMapItemSearchParams();
				executeSearch(false);
				Toast.makeText(getActivity(), "Searching...", Toast.LENGTH_SHORT).show();
				App.mapListener.removeOnPositionUpdateListener(SearchMapitemFragment.this);
				((MainActivity)getActivity()).hideTheKeyboard();

			}
		});

		ui.id(R.id.button_all).clicked(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setTypeAll();
				setMapItemSearchParams();
				ui.id(R.id.button_all).activated(true);
				ui.id(R.id.button_hotspot).activated(false);
				ui.id(R.id.button_route).activated(false);
				App.mapListener.searchForNewMapItems();
			}
		});

		ui.id(R.id.button_hotspot).clicked(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setTypeHotspot();
				setMapItemSearchParams();
				ui.id(R.id.button_all).activated(false);
				ui.id(R.id.button_hotspot).activated(true);
				ui.id(R.id.button_route).activated(false);
				App.mapListener.searchForNewMapItems();
			}
		});

		ui.id(R.id.button_route).clicked(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setTypeRoute();
				setMapItemSearchParams();
				ui.id(R.id.button_all).activated(false);
				ui.id(R.id.button_hotspot).activated(false);
				ui.id(R.id.button_route).activated(true);
				App.mapListener.searchForNewMapItems();
			}
		});
		
		ui.id(R.id.button_event).clicked(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setEventSearch(!getEventSearch());
				Log.debug(SearchMapitemFragment.this, "eventsearch="+getEventSearch());
				setMapItemSearchParams();
				ui.id(R.id.button_event).activated(getEventSearch());
				if(getEventSearch()) ui.id(R.id.eventtime_field).visible();
				else ui.id(R.id.eventtime_field).gone();
				App.mapListener.searchForNewMapItems();
			}
		});

		//Open Search List
		ui.id(R.id.button_list).clicked(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setMapItemSearchParams();
				executeSearch(true);
				Toast.makeText(getActivity(), "Searching...", Toast.LENGTH_SHORT).show();
				((MainActivity)getActivity()).hideTheKeyboard();
			}
		});

		// Date Time Chooser
		ui.id(R.id.button_begin_eventtime).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePicker(BEGIN_TIME);
			}
		});

		ui.id(R.id.button_end_eventtime).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePicker(END_TIME);
			}
		});
	}

	private void showDatePicker(int type) {
		DatePickerFragment dateFragment = new DatePickerFragment();
		
		// create bundle
		Bundle args = new Bundle();
		if(type == BEGIN_TIME) {
			// apparently month ranges 0-11, so sub 1
			args.putInt("year", begin_year);
			args.putInt("month", begin_month-1);
			args.putInt("day", begin_day);
		}
		else {
			// apparently month ranges 0-11, so sub 1
			args.putInt("year", end_year);
			args.putInt("month", end_month-1);
			args.putInt("day", end_day);
		}
		dateFragment.setArguments(args);

		// Set Call back to capture selected date
		if(type == BEGIN_TIME) dateFragment.setCallBack(beginTimeListener);
		else dateFragment.setCallBack(endTimeListener);
		dateFragment.show(getFragmentManager(), "Date Picker");
	}

	OnDateSetListener beginTimeListener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// apparently month ranges 0-11, so add 1
			setTime(BEGIN_TIME, dayOfMonth, monthOfYear+1, year);
			if(getBeginDate().after(getEndDate())) {
				setTime(BEGIN_TIME, end_day, end_month, end_year);
				Toast.makeText(getActivity(), "Begin date can't be after end date...", Toast.LENGTH_LONG).show();
			}
			setMapItemSearchParams();
			App.mapListener.searchForNewMapItems();
		}
	};
	
	OnDateSetListener endTimeListener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// apparently month ranges 0-11, so add 1
			setTime(END_TIME, dayOfMonth, monthOfYear+1, year);
			if(getEndDate().before(getBeginDate())) {
				setTime(END_TIME, begin_day, begin_month, begin_year);
				Toast.makeText(getActivity(), "End date can't be before begin date...", Toast.LENGTH_LONG).show();
			}
			setMapItemSearchParams();
			App.mapListener.searchForNewMapItems();
		}
	};
	
	protected boolean isQueryNeeded() {
		return false;
	};

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

	private void executeSearch(boolean listsearch){
		String name = ui.id(R.id.name_field).getText().toString();
		int position = ui.id(R.id.category_spinner).getSelectedItemPosition();
		String category = dbCategoryList.get(position).toString();

		Double[] boundingCircle = App.mapListener.getBoundingCircle();
		String bounding = boundingCircle[0].toString()+","+boundingCircle[1].toString()+","+boundingCircle[2].toString();

		if (listsearch) {
			((MainActivity) App.activity)
			.addFragmentController(SearchListFragment.newInstance(type, name, category, boundingCircle, getEventSearch(), getBeginDate(), getEndDate()));
		} else {
			search = new SearchForm();
			search.execute(type, name, category, bounding, 
					booleanToString(getEventSearch()), 
					Integer.toString(begin_year), 
					Integer.toString(begin_month), 
					Integer.toString(begin_day), 
					Integer.toString(end_year), 
					Integer.toString(end_month), 
					Integer.toString(end_day));
		}
	}

	private class SearchForm extends
			android.os.AsyncTask<String, Integer, List<MapItem>> {

		@Override
		protected List<MapItem> doInBackground(String... params) {

			List<MapItem> result = new ArrayList<MapItem>();
					
			try {
				if (stringToBoolean(params[4])) {
					Date beginDate, endDate;
					
					// apparently month ranges 0-11, so sub 1
					beginDate = new GregorianCalendar(Integer.parseInt(params[5]), 
							Integer.parseInt(params[6])-1, Integer.parseInt(params[7]), 0, 0, 0).getTime();

					// apparently month ranges 0-11, so sub 1
					endDate = new GregorianCalendar(Integer.parseInt(params[8]), 
							Integer.parseInt(params[9])-1, Integer.parseInt(params[10]), 23, 59, 59).getTime();

					Log.debug(SearchMapitemFragment.this, "Searching for Mapitems with Events between "+beginDate+" and "+endDate +
							" and with parameters: "+ params[0]+", "+ params[1]+", "+params[2]+", "+params[3]);
					
					result = MapItemAPI.getInstance().getHotspotsWithEvents(params[0], params[1], params[2], params[3], 
							beginDate, endDate);
				} else {
					Log.debug(SearchMapitemFragment.this, "Searching for Mapitems with parameters: "+ params[0]+", "+ params[1]+", "+params[2]+", "+params[3]);
					result = MapItemAPI.getInstance().getHotspotsExtended(params[0], params[1], params[2], params[3]);
				}
					
			} catch (APIException e) {

				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(List<MapItem> result) {

			if (result != null && !result.isEmpty()) {
				App.mapListener.changeMapItems(result);
				App.mapListener.showMapItem(result.get(0));
			}
			else 
				Toast.makeText(getActivity(), "No Results found", Toast.LENGTH_LONG).show();

			super.onPostExecute(result);
		}
	}
	
	private void setTime(int type, int day, int month, int year) {
		if(type == BEGIN_TIME) {
			begin_day = day;
			begin_month = month;
			begin_year = year;
			ui.id(R.id.button_begin_eventtime).text(day+"."+month+"."+year);
		}
		else if(type == END_TIME) {
			end_day = day;
			end_month = month;
			end_year = year;
			ui.id(R.id.button_end_eventtime).text(day+"."+month+"."+year);
		}
		
	}

	private void setTypeAll() {
		this.type = TYPE_ALL;
	}

	private void setTypeRoute() {
		this.type = TYPE_ROUTE;
	}

	private void setTypeHotspot() {
		this.type = TYPE_HOTSPOT;
	}
	
	private void setEventSearch(boolean e) {
		this.eventSearch = e;
	}
	
	private boolean getEventSearch() {
		return this.eventSearch;
	}

	private Date getBeginDate() {
		// apparently month ranges 0-11, so sub 1
		return new GregorianCalendar(begin_year, begin_month-1, begin_day, 0, 0, 0).getTime();
	}
	
	private Date getEndDate() {
		// apparently month ranges 0-11, so sub 1
		return new GregorianCalendar(end_year, end_month-1, end_day, 23, 59, 59).getTime();
	}
	
	private void setMapItemSearchParams() {
		String name = ui.id(R.id.name_field).getText().toString();
		int position = ui.id(R.id.category_spinner).getSelectedItemPosition();
		String category = dbCategoryList.get(position).toString();
		
		Date beginDate = getBeginDate();
		Date endDate = getEndDate();

		App.mapListener.setSearchParams(this.type, name, category, getEventSearch(), beginDate, endDate);
	}

	@Override
	public void onCameraPositionUpdate() {
		Log.info(this, "moved cameera");
		setMapItemSearchParams();
	}

	@Override
	public void onPositionUpdate(Coordinate coordinate) {
	}
	
	private String booleanToString(boolean b) {
		if(b) return "true";
		else return "false";
	}
	
	private boolean stringToBoolean(String s) {
		if(s.equals("true")) return true;
		else return false;
	}
}