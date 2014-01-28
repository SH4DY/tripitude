package at.tuwien.ase.tripidude.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.api.vo.RestAuthNonceSessionObj;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.models.Coordinate;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.Log.LogSource;
import at.tuwien.ase.tripidude.utils.UiQuery;

import com.androidquery.AQuery;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxCallback;

public class TripitudeAPI implements LogSource {
	
	protected static TripitudeAPI instance;
	protected static UiQuery aq;
	protected static String BASE_URL = App.resources.getString(R.string.backend_base_url) + "api/";
	protected static String BASE_URL_WEB = App.resources.getString(R.string.backend_base_url);
	protected static ObjectMapper objectMapper;
	protected static String salt = "qsease08";
	private static RestAuthNonceSessionObj restSession = null;
	
	public static TripitudeAPI getInstance() {
		if (instance != null)
			return instance;
		else 
			return new TripitudeAPI();
	} 
	
	protected TripitudeAPI() {
		aq = new UiQuery(App.activity);
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	protected AjaxCallback<String> getNewQuery() {
		
		AjaxCallback<String> cb = new AjaxCallback<String>();
		
		//set type header
		cb.type(String.class).header("Content-Type", "application/json");
		AbstractAjaxCallback.setTimeout(10000);
		//set session cookie
        if (restSession != null) {       	
        	cb.cookie("JSESSIONID", restSession.getSessionId());
        }
	
		return cb;
	}
	
	protected AjaxCallback<String> getNewTextQuery() {
		AjaxCallback<String> cb = new AjaxCallback<String>();
		//set type header
		cb.type(String.class);
		//set session cookie
        if (restSession != null) {       	
        	cb.cookie("JSESSIONID", restSession.getSessionId());
        }
	
		return cb;
	}
	
	protected <T> T get(String url, Class<T> mappingType) throws APIException {
		//call API
        AjaxCallback<String> cb = getNewQuery();
		cb.url(url);
		
		RestErrorResponse errors = new RestErrorResponse();		
		T response = performQuery(cb, mappingType, errors);
		
		//check errors
    	if (errors.status >= 400) {
    		throw new APIException(errors);
		}

    	return response;
	}
	
	protected <T, E> E post(String url, T postObject, Class<E> mappingType) throws APIException {
		//call API
        AjaxCallback<String> cb = getNewQuery();
		cb.url(url);
		
		//convert to JSON
		String json = null;

		try {
			json = objectMapper.writeValueAsString(postObject);
		} catch (JsonGenerationException e) {
			throw new APIException(e.getMessage());
		} catch (JsonMappingException e) {
			throw new APIException(e.getMessage());
		} catch (IOException e) {
			throw new APIException(e.getMessage());
		}
		
		//create POST BODY
		HttpEntity entity = null;
		try {
			entity = new StringEntity(json);
		} catch (UnsupportedEncodingException e) {
			throw new APIException(e.getMessage());
		}
		
		//add POST BODY
		Map<String, Object> params = new HashMap<String, Object>();
        params.put(AQuery.POST_ENTITY, entity);
        
        cb.url(url).params(params);
		
		RestErrorResponse errors = new RestErrorResponse();		
		E response = performQuery(cb, mappingType, errors);
		
		//check errors
    	if (errors.status >= 400) {
    		throw new APIException(errors);
		}

    	return response;
	}
	
    protected <T> T performQuery(AjaxCallback<String> cb, Class<T> mappingType, RestErrorResponse errors) {
		
    	//perform request
    	aq.sync(cb);
 		
    	T responseObject = null;

    	if (cb.getResult() != null) {
			
    		
			try {
				responseObject = objectMapper.readValue(cb.getResult().toString(), mappingType);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					//map to error object
					TypeReference<Collection<FieldError>> ref = new TypeReference<Collection<FieldError>>(){};
					RestErrorResponse responseErrors = objectMapper.readValue(cb.getResult().toString(), ref);
					
					errors = responseErrors;					
				} catch (Exception e1) {
					Log.error(this, e1.getMessage());
				}
				
			} 
		}
       
		return responseObject;
	}
    
    public static String getBaseUrlWeb() {
    	return BASE_URL_WEB;
    }
	
	@Override
	public String getLogSourceName() {
		return this.getClass().getSimpleName();
	}

	protected static RestAuthNonceSessionObj getRestSession() {
		return restSession;
	}

	protected static void setRestSession(RestAuthNonceSessionObj restSession) {
		TripitudeAPI.restSession = restSession;
	}
	
	/**  
	 * @param coord1
	 * @param coord2
	 * @return distance between two coordinates in meter
	 */
	public static double distance(Coordinate coord1, Coordinate coord2) {
		double lat1 = coord1.getLatitude();
		double lon1 = coord1.getLongitude();
		double lat2 = coord2.getLatitude();
		double lon2 = coord2.getLongitude();

		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515 * 1.609344 * 1000;

		return dist;
	}
	
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}
	
	private static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
	
}
