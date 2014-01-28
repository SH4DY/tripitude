package at.tuwien.ase.tripidude.api;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.EventCategory;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.models.User;
import at.tuwien.ase.tripidude.utils.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;

/**
 * 
 * @author Thomas Braunsberger
 * 
 */
public class EventAPI extends TripitudeAPI {

	private static EventAPI instance;

	public static EventAPI getInstance() {
		if (instance != null)
			return instance;
		else
			return new EventAPI();
	}

	private EventAPI() {
		super.getInstance();
	}
	
	public Boolean createEvent(String name, Date time, String description, List<EventCategory> eventCategory, MapItem mapItem){
		String url = BASE_URL + "event/";

		Event event = new Event();
		event.setName(name);
		event.setTime(time);
		event.setDescription(description);
		event.setMapItem(mapItem);
		event.setUser(UserAPI.getCurrentUser());
		event.setCategories(eventCategory);
		String eventJson = null;
		try {
			eventJson = objectMapper.writeValueAsString(event);
			Log.debug("EventAPI", eventJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		HttpEntity entity = null;
		try {
			entity = new StringEntity(eventJson);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//add POST BODY
		Map<String, Object> params = new HashMap<String, Object>();
        params.put(AQuery.POST_ENTITY, entity);
        
        //call API
        AjaxCallback<String> cb = getNewQuery();
		cb.url(url).params(params);
		
		RestErrorResponse errors = new RestErrorResponse();		
		performQuery(cb, Event.class, errors);
		
		//check errors
    	if (errors.getStatus() >= 400) {
			return false;
		}
		
		return true;
	}
	
	public Event getEvent(long id) throws APIException {
		return get(BASE_URL + "event/"+id, Event.class);
	}
	
	public List<EventCategory> getEventCategories() throws APIException{
		EventCategory[] response = get(BASE_URL + "eventcategory", EventCategory[].class);
		return Arrays.asList(response);
	}
	
	public List<User> getAttendingUsers(long id) throws APIException{
		User[] response = get(BASE_URL + "attendingUsers/" + id, User[].class);
		return new ArrayList<User>(Arrays.asList(response));
	}
	
	public Boolean attendEvent(long id) throws APIException {
		return post(BASE_URL + "event/"+id+"/attend", null, Boolean.class);
	}
	
	public Boolean unattendEvent(long id) throws APIException {
		return post(BASE_URL + "event/"+id+"/unattend", null, Boolean.class);
	}
}