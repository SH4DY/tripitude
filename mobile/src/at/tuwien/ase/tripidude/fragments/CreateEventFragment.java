package at.tuwien.ase.tripidude.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.EventAPI;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.models.EventCategory;
import at.tuwien.ase.tripidude.models.MapItem;

public class CreateEventFragment extends FragmentController implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
	private SaveEvent save;

	private Integer mYear, mMonth, mDay;
	private Integer mHour, mMinute;
	
	private List<EventCategory> eventCategories;
	private List<CharSequence> eventCategoryNames;
	
	private static String _eventName, _eventDescription;
	private static Date _eventDate;
	private static List<EventCategory> _eventCategories;
	private static MapItem _eventMapItem;

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_create_event;
	}
	
	@Override
	protected void onCreate() throws Exception {
		_eventMapItem = (MapItem)((MainActivity) getActivity()).getCurrentMapItem();
		ui.id(R.id.event_location_field).text(_eventMapItem.getTitle());
		
		ui.id(R.id.save_event_button).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mYear == null || mHour == null){
					Toast.makeText(getActivity(), getString(R.string.please_enter_date),
							Toast.LENGTH_SHORT).show();
				}
				else if(ui.id(R.id.event_name_field).getText().toString().length() < 5 ||
						ui.id(R.id.event_name_field).getText().toString().length() > 50){
					Toast.makeText(getActivity(), getString(R.string.name_length), Toast.LENGTH_SHORT).show();
				}
				else{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm", Locale.getDefault());
					String dateString;
					if((mMonth+1) >= 10 && mDay >= 10)
						dateString = mYear + "-" + (mMonth+1) + "-" + mDay;
					else if((mMonth+1) < 10 && mDay >= 10)
						dateString = mYear + "-" + "0" + (mMonth+1) + "-" + mDay;
					else if((mMonth+1) < 10 && mDay < 10)
						dateString = mYear + "-" + "0" + (mMonth+1) + "-" + "0" + mDay;
					else
						dateString = mYear + "-" + (mMonth+1) + "-" + "0" + mDay;
					dateString += "T";
					if(mHour < 10)
						dateString += "0" + mHour;
					else
						dateString += mHour;
					dateString += ":";
					if(mMinute < 10)
						dateString += "0" + mMinute;
					else
						dateString += mMinute;
					try {
						_eventDate = sdf.parse(dateString);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					_eventName = ui.id(R.id.event_name_field).getText().toString();
					_eventDescription = ui.id(R.id.event_description_field).getText().toString();

					Integer eventCategory = ui.id(R.id.event_category_spinner).getSelectedItemPosition();
					_eventCategories = new ArrayList<EventCategory>();
					_eventCategories.add(eventCategories.get(eventCategory));
					
					save = new SaveEvent();
					save.execute();
					Toast.makeText(getActivity(), getString(R.string.saving),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		ui.id(R.id.button_open_date_picker).clicked(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				int day = c.get(Calendar.DAY_OF_MONTH);

				DatePickerDialog dialog = new DatePickerDialog(getActivity(), CreateEventFragment.this, year, month, day);
				dialog.show();
			}
		});
		
		ui.id(R.id.event_date_field).clicked(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 final Calendar c = Calendar.getInstance();
			     int year = c.get(Calendar.YEAR);
			     int month = c.get(Calendar.MONTH);
			     int day = c.get(Calendar.DAY_OF_MONTH);
			        
				DatePickerDialog dialog = new DatePickerDialog(getActivity(), CreateEventFragment.this, year, month, day);
				dialog.show();
			}
		});
		
		ui.id(R.id.button_open_time_picker).clicked(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				TimePickerDialog dialog = new TimePickerDialog(getActivity(), CreateEventFragment.this, hour, minute, false);
				dialog.show();
			}
		});
		
		ui.id(R.id.event_time_field).clicked(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				TimePickerDialog dialog = new TimePickerDialog(getActivity(), CreateEventFragment.this, hour, minute, false);
				dialog.show();
			}
		});
	}

	@Override
	protected void onQuery() throws Exception {
		eventCategories = EventAPI.getInstance().getEventCategories();
		eventCategoryNames = new ArrayList<CharSequence>();
		for(EventCategory ec : eventCategories){
			if(ec.getName().equals("arts_entertainment"))
				eventCategoryNames.add("Arts & Entertainment");
			else if(ec.getName().equals("eat_together"))
				eventCategoryNames.add("Lunch / Dinner");
			else if(ec.getName().equals("shopping"))
				eventCategoryNames.add("Shopping");
			else if(ec.getName().equals("dancing"))
				eventCategoryNames.add("Dancing");
			else if(ec.getName().equals("party"))
				eventCategoryNames.add("Party");
			else if(ec.getName().equals("sightseeing"))
				eventCategoryNames.add("Sightseeing");
			else if(ec.getName().equals("other"))
				eventCategoryNames.add("Other");
		}
	}

	@Override
	protected void onShow() throws Exception {
		Spinner spinner = (Spinner) getView().findViewById(
				R.id.event_category_spinner);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), 
				android.R.layout.simple_spinner_item, eventCategoryNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected View getLoadingView() {
		return null;
	}

	@Override
	protected View getRealView() {
		return null;
	}

	private class SaveEvent extends
	android.os.AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			return EventAPI.getInstance().createEvent(_eventName, _eventDate, _eventDescription, _eventCategories, _eventMapItem);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result)
				Toast.makeText(getActivity(), getString(R.string.event_saved), Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
			if(result)
				getActivity().onBackPressed();
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		mYear = year;
		mMonth = month;
		mDay = day;
		if((month+1) >= 10 && day >= 10)
			ui.id(R.id.event_date_field).text(mYear + " - " + (mMonth+1) + " - " + mDay);
		else if((month+1) < 10 && day >= 10)
			ui.id(R.id.event_date_field).text(mYear + " - " + "0" + (mMonth+1) + " - " + mDay);
		else if((month+1) < 10 && day < 10)
			ui.id(R.id.event_date_field).text(mYear + " - " + "0" + (mMonth+1) + " - " + "0" + mDay);
		else
			ui.id(R.id.event_date_field).text(mYear + " - " + (mMonth+1) + " - " + "0" + mDay);

	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		mHour = hourOfDay;
		mMinute = minute;
		if(minute >= 10)
			ui.id(R.id.event_time_field).text(mHour + ":" + mMinute);
		else
			ui.id(R.id.event_time_field).text(mHour + ":" + "0" + mMinute);
	}
}
