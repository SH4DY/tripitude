package at.tuwien.ase.tripidude.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import at.tuwien.ase.tripidude.models.Comment;
import at.tuwien.ase.tripidude.models.Coordinate;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.File;
import at.tuwien.ase.tripidude.models.Hotspot;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.models.MapItemCategory;
import at.tuwien.ase.tripidude.models.Rating;
import at.tuwien.ase.tripidude.models.RatingCacheMapItem;
import at.tuwien.ase.tripidude.models.Route;
import at.tuwien.ase.tripidude.utils.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;

public class MapItemAPI extends TripitudeAPI {

	public static final String CATEGORIES_ALL = "All Categories";
	
	private static MapItemAPI instance;
	private static final int maxBoundingCircleSearch = 30000;
	

	public static MapItemAPI getInstance() {
		if (instance != null)
			return instance;
		else
			return new MapItemAPI();
	}

	private MapItemAPI() {
		super.getInstance();
	}
	
	public List<MapItem> getMapItemsForBoundingCircle(Double[] boudingCircle) throws APIException {
		if (boudingCircle == null) 
			throw new APIException();
		MapItem[] response = get(BASE_URL + "mapitem?bounding_circle="+boudingCircle[0].toString()+","+boudingCircle[1].toString()+","+boudingCircle[2].toString(), MapItem[].class);
		if (response == null) 
			throw new APIException();
		return Arrays.asList(response);
	}

	public List<MapItem> getAllMapItems() throws APIException {
		MapItem[] response = get(BASE_URL + "mapitem", MapItem[].class);
		if (response == null) 
			throw new APIException();
		return Arrays.asList(response);
	}

	public List<Hotspot> getAllHotspots() throws APIException {
		Hotspot[] response = get(BASE_URL + "mapitem?types=HOTSPOT",
				Hotspot[].class);
		if (response == null) 
			throw new APIException();
		return Arrays.asList(response);
	}

	public List<Route> getAllRoutes() throws APIException {
		Route[] response = get(BASE_URL + "mapitem?types=ROUTE", Route[].class);
		if (response == null) 
			throw new APIException();
		return Arrays.asList(response);
	}
	
	public List<MapItem> getHotspots(String type, String name, String category, String bounding) throws APIException {
		String geturl = this.getSearchUrl(type, name, category, bounding);
		
		MapItem[] response = get(geturl, MapItem[].class);
		
		if (response == null || response.length == 0) return new ArrayList<MapItem>();
		
		return Arrays.asList(response);
	}
	
	
	public List<MapItem> getHotspotsExtended(String type, String name, String category, String bounding) throws APIException {
		String geturl = this.getSearchUrl(type, name, category, bounding);
		
		MapItem[] response = get(geturl, MapItem[].class);
		
		
		//If no spot is found extend search
		if(response.length == 0) {

			String latlong = bounding.substring(0, bounding.lastIndexOf(','));
			String newbounding = latlong+","+maxBoundingCircleSearch;
			
			geturl = this.getSearchUrl(type, name, category, newbounding);
			response = get(geturl, Hotspot[].class);
		}

		return Arrays.asList(response);
	}
	
	public List<MapItem> getHotspotsWithEvents(String type, String name, String category, String bounding, Date beginDate, Date endDate) throws APIException {
		String url = this.getEventSearchUrl(type, name, category, bounding, beginDate, endDate);
		Log.debug(MapItemAPI.this, "Query: "+url);
		MapItem[] response = get(url, MapItem[].class);
		
		//If no spot is found extend search
		if (response == null || response.length == 0) {

			String latlong = bounding.substring(0, bounding.lastIndexOf(','));
			String newbounding = latlong + "," + maxBoundingCircleSearch;

			url = this.getEventSearchUrl(type, name, category, newbounding, beginDate, endDate);
			response = get(url, Hotspot[].class);
			if(response == null || response.length == 0) return new ArrayList<MapItem>();
		}
		
		return Arrays.asList(response);
	}

	private String getSearchUrl(String type, String name, String category, String bounding) throws APIException {
		String url = BASE_URL+"mapitem?";

		if(!bounding.trim().equals("")) {
			url += "bounding_circle="+bounding;
		}
		
		//Set Type
		if (type.equals("hotspot")) {
			url += "&types=HOTSPOT";
		} else if (type.equals("route")) {
			url += "&types=ROUTE";
		}

		// Set Title
		if (!name.trim().equals("")) {
			url += "&title_like=" + name;
		}

		// Set Category
		if (!category.equals("All Categories")) {
			url += "&category_ids="
					+ this.getCategoryByName(category).getId().toString();
		}
		return url;
	}
	
	private String getEventSearchUrl(String type, String name, String category,
			String bounding, Date beginDate, Date endDate) throws APIException {
		
		String url = this.getSearchUrl(type, name, category, bounding);
		
		url += "&events=1";
		
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
		
		url += "&begin_date="+format.format(beginDate);
		url += "&end_date="+format.format(endDate);
		
		return url;
	}
	
	public List<MapItemCategory> getCategories() throws APIException{
		List<MapItemCategory> found = Arrays.asList(get(BASE_URL + "mapitemcategory", MapItemCategory[].class));
		if(found == null){
			throw new APIException("No MapItemCategories found ");
		}
		return found;
	}
	
	public MapItemCategory getCategoryByName(String name) throws APIException {
		List<MapItemCategory> found =  Arrays.asList(get(BASE_URL + "mapitemcategory?title="+name, MapItemCategory[].class));
		
		if(found == null) {
			throw new APIException("MapItemCategory not found");
		}
		
		return found.get(0);
	}

	public Route getRouteDetail(long id) throws APIException {
		return get(BASE_URL + "route/" + id, Route.class);
	}

	public Hotspot getHotspot(long id) throws APIException {
		return get(BASE_URL + "mapitem/" + id, Hotspot.class);
	}
	
	public Route getRoute(long id) throws APIException {
		return get(BASE_URL + "mapitem/" + id, Route.class);
	}
	
	public List<Event> getEvents(Long id) throws APIException {
		Event[] response = get(BASE_URL + "mapitem/"+id+"/events", Event[].class);
		return Arrays.asList(response);
	}
	
	public RatingCacheMapItem postMapiItemRating(long id, Integer ratingValue) throws APIException {
		
		String url = BASE_URL + "mapitem/" + id + "/rating";
		
		Rating rating = new Rating();
		rating.setRating(ratingValue);
		
		return post(url, rating, RatingCacheMapItem.class);
	}

	public Hotspot createHotspot(String title, String description,
		List<String> categories, Double cost, Double lat, Double lon) throws APIException {
		String url = BASE_URL + "hotspot/";

		Coordinate coordinate = new Coordinate(lat, lon);
		Hotspot hotspot = new Hotspot(title, description, cost);
		
		List<MapItemCategory> mapItemCategories = new ArrayList<MapItemCategory>();
		for (String stringCategory : categories) {
			MapItemCategory mapItemCategory = getCategoryByName(stringCategory);
			mapItemCategories.add(mapItemCategory);
		}
		hotspot.setCategories(mapItemCategories);
		
		hotspot.setCoordinate(coordinate);

		String hotspotJson = null;
		try {
			hotspotJson = objectMapper.writeValueAsString(hotspot);
			Log.debug("HotspotAPI, generated JSON: ", hotspotJson);
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpEntity entity = null;
		try {
			entity = new StringEntity(hotspotJson);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// add POST BODY
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(AQuery.POST_ENTITY, entity);

		// call API
		AjaxCallback<String> cb = getNewQuery();
		cb.url(url).params(params);

		RestErrorResponse errors = new RestErrorResponse();
		Hotspot hotspotFromResponse = performQuery(cb, Hotspot.class, errors);

		// check errors
		if (errors.getStatus() > 0) {
			return new Hotspot();
		}
		Log.debug("MapItemAPI", hotspotFromResponse.toString());
		return hotspotFromResponse;
	}
	

	public MapItem updateHotspot(Hotspot hotspotToUpdate) throws APIException{
		String url = BASE_URL + "hotspot/";

		String hotspotJson = null;
		try {
			hotspotJson = objectMapper.writeValueAsString(hotspotToUpdate);
			Log.debug("MapItemAPI, generated JSON from Hotspot to update: ", hotspotJson);
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpEntity entity = null;
		try {
			entity = new StringEntity(hotspotJson);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// add POST BODY
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(AQuery.POST_ENTITY, entity);

		// call API
		AjaxCallback<String> cb = getNewQuery();
		cb.url(url).params(params);
		cb.method(AQuery.METHOD_PUT);

		RestErrorResponse errors = new RestErrorResponse();
		Hotspot hotspotFromResponse = performQuery(cb, Hotspot.class, errors);

		// check errors
		if (errors.getStatus() > 0) {
			return new Hotspot();
		}
		Log.debug("MapItemAPI", hotspotFromResponse.toString());
		return hotspotFromResponse;
	}

	public Boolean saveImagesToMapItem(String mapItemId, List<Bitmap> bitmaps) throws APIException {
		
		String url = BASE_URL + "imagefile/" + mapItemId;
		
		Boolean success = true;
		
		for (Bitmap bitmap : bitmaps) {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
			byte[] ba = bao.toByteArray();
			String encodedImage = Base64.encodeToString(ba, Base64.DEFAULT);

			File file =  post(url, encodedImage, File.class);
			
			if (file == null) {
				success = false;
			}
		}
		
		return success;
	}
	

	public List<Bitmap> getImagesFromMapItem(Long id) throws APIException {
		String url = BASE_URL + "imagefile/" + id;
		List<Bitmap> bitmaps = new ArrayList<Bitmap>();
		Log.info("MapItemAPI", "Getting Images of MapItem");
		String[] response = get(url, String[].class);
		
		
		for (String encodedImage : response) {
			byte[] imageAsBytes = Base64.decode(encodedImage.getBytes(), Base64.DEFAULT);
			bitmaps.add(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
		}
		Log.info("MapItemAPI", "Decoded, now back to the Fragment");
		return bitmaps;
	}
	
	public Rating getRatingByMapItemAndUser(Long mapItemId, Long userId) throws APIException {
		String url = BASE_URL + "mapitem/" + mapItemId + "/rating/user/" + userId;

		Rating rating = get(url, Rating.class);
		return rating;
	}
	
	public Comment postMapItemComment(Long mapItemId, String text) throws APIException {
		
		if (mapItemId == null) {
			return null;
		}
		
		String url = BASE_URL + "mapitem/" + mapItemId + "/comment";
		
		Comment comment = new Comment(text);
		
		return post(url, comment, Comment.class);
	}
	
	public Route createRoute(Route route) {
		String url = BASE_URL + "route/";

		String routeJson = null;
		try {
			routeJson = objectMapper.writeValueAsString(route);
			Log.debug(this, routeJson);
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpEntity entity = null;
		try {
			entity = new StringEntity(routeJson);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// add POST BODY
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(AQuery.POST_ENTITY, entity);

		// call API
		AjaxCallback<String> cb = getNewQuery();
		cb.url(url).params(params);

		RestErrorResponse errors = new RestErrorResponse();
		Route routeFromResponse = performQuery(cb, Route.class, errors);

		// check errors
		if (errors.getStatus() > 0) {
			return new Route();
		}
		Log.debug("MapItemAPI", routeFromResponse.toString());
		return routeFromResponse;
	}
	
	public String createHistoryItem(long mapItemId) {
		String result = "Check-In successfull";
		boolean checkin = false;
		
		try {
			checkin = post(BASE_URL+ "mapitem/" + mapItemId + "/historyitem", null, Boolean.class);
		} catch (APIException e) {
			e.printStackTrace();
			result = "Check-In failed because of error";
		}
		
		if(checkin) {
			return result;
		} else {
			return "Already Checked-In";
		}
	}
	
	public List<Coordinate> getDirections(MapItem start, MapItem end) {
		Log.startTimer();
	    String url = "http://maps.googleapis.com/maps/api/directions/xml?origin=" + start.getCoordinate().getLatitude() + "," + start.getCoordinate().getLongitude() + "&destination=" + end.getCoordinate().getLatitude() + "," + end.getCoordinate().getLongitude()
	            + "&sensor=false&units=metric&mode=walking";
	    String tag[] = {"lat", "lng"};
	    List<Coordinate> coordinates = new ArrayList<Coordinate>();
	    HttpResponse response = null;
	    try {
	        HttpClient httpClient = new DefaultHttpClient();
	        HttpContext localContext = new BasicHttpContext();
	        HttpPost httpPost = new HttpPost(url);
	        response = httpClient.execute(httpPost, localContext);
	        InputStream in = response.getEntity().getContent();
	        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = builder.parse(in);
	        if (doc != null) {
	            NodeList nl1, nl2;
	            nl1 = doc.getElementsByTagName(tag[0]);
	            nl2 = doc.getElementsByTagName(tag[1]);
	            if (nl1.getLength() > 0) {
	                for (int i = 0; i < nl1.getLength()-4; i++) {
	                    Node node1 = nl1.item(i);
	                    Node node2 = nl2.item(i);
	                    double lat = Double.parseDouble(node1.getTextContent());
	                    double lng = Double.parseDouble(node2.getTextContent());
	                    coordinates.add(new Coordinate(lat, lng));
	                }
	            } else {
	            	Log.stopTimer(this, "NO POINTS FOUND: " + url);
	            }
	        }
	        Log.stopTimer(this, "REQUEST DONE: " + url);
	    } catch (Exception e) {
	    	Log.stopTimer(this, "REQUEST FAILED: " + url);
	        e.printStackTrace();
	    }
	    return coordinates;
	}
}
