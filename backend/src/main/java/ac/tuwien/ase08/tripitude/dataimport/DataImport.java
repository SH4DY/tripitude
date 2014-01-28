package ac.tuwien.ase08.tripitude.dataimport;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

import ac.tuwien.ase08.tripitude.entity.Comment;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.EventCategory;
import ac.tuwien.ase08.tripitude.entity.HistoryItem;
import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.MapItemCategory;
import ac.tuwien.ase08.tripitude.entity.Role;
import ac.tuwien.ase08.tripitude.entity.Route;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.search.BoundingCircleCriteria;
import ac.tuwien.ase08.tripitude.search.MapItemSearchCriteria;
import ac.tuwien.ase08.tripitude.service.interfaces.ICommentService;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IEventService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHistoryItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IHotspotService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemCategoryService;
import ac.tuwien.ase08.tripitude.service.interfaces.IMapItemService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IRouteService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class DataImport {
	
	@Autowired	
    private IHotspotService hotspotService;
	@Autowired
	private IUserService userService;
	@Autowired 
	private IRoleService roleService;
	@Autowired	
    private ICoordinateService coordinateService;
	@Autowired	
    private IMapItemCategoryService mapItemCategoryService;
	@Autowired
	private IRouteService routeService;
	@Autowired
	private IEventService eventService;
	@Autowired
	private IEventCategoryService eventCategoryService;
	@Autowired
	private ICommentService commentService;
	@Autowired
	private IHistoryItemService historyItemService;
	@Autowired
	private IMapItemService mapItemService;
	
	
	@Autowired
	private SimpleGPXParser parser;
	
	
	@Autowired 
	private MessageSource messageSource;
	
	private static final String GOOGLE_PLACES_URL = 
			"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={lat},{long}&radius={radius}&types={type}&sensor=false&key={apiKey}";
	
	/**
	 * Create initial user and roles
	 * @return
	 */
	public String importInitialUserData() {
		
		String message = "";
		
		//generate roles
		List<String> roleList = new ArrayList<String>();
		roleList.add("AUTHENTICATED");
		roleList.add("ADMIN");
		
		for (String roleString : roleList) {
			
			Role role = roleService.getRoleByRole(roleString);
			
			if (role == null) {
				role = new Role();
				role.setRole(roleString);
				roleService.add(role);
				
				message += "Added Role " + roleString + " <br />";
			}
		}
		
		//generate user
		User user = userService.getUserByEmail("importFrom@google.com");
		
		if (user == null) {
			user = new User();
			user.setName("Google Import");
			user.setEmail("importFrom@google.com");
			user.setPassword("password");
			
			Role role = roleService.getRoleByRole("AUTHENTICATED");
			user.setRole(role);
			userService.add(user);
			
			message += "Added User Google Import, importFrom@google.com, password <br />";
		}
		
		//some real users
		message += addUser("Ramon", "ramon@tripitude.com", "ramon123", 1000L, "Tourist");
		message += addUser("Gerhard", "gerdsch@tripitude.com", "gerdsch123", 10000L, "Been there, done that.");
		message += addUser("Georg", "georg@tripitude.com", "georg123", 6000L, "Explorer");
		message += addUser("Matthias", "matthias@tripitude.com", "matthias123", 3500L, "Traveler");
		message += addUser("Martin", "martin@tripitude.com", "martin123", 100000L, "Cheater");
		message += addUser("Thomas", "thomas@tripitude.com", "thomas123", 8000L, "Explorer");
		
		message += addUser("Sabine", "sabine@hotmail.com", "sabine123", 20L, "Newbie");
		message += addUser("Michaela", "michi@gmx.net", "michi123", 100L, "Newbie");
		message += addUser("Romana", "romy@facebook.com", "romana123", 500L, "Tourist");
		
		return message;
	}
	
	private String addUser(String name, String email, String password, Long points, String rank){
		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setPassword(password);
		user.setRank(rank);
		user.setPoints(points);
		Role role = roleService.getRoleByRole("AUTHENTICATED");
		user.setRole(role);
		userService.add(user);
		
		return "Added User " + name + ", " + email + ", " + password + "<br />";
	}
	
	/**
	 * Import initial MapItemCategories
	 * @return
	 */
	public String importInitialMapItemCategories() {
		
		String message = "";
		
		if (mapItemCategoryService.list().size() > 5) {
			return "Already enough MapItemCategories" + " <br />";
		};
		
		List<String> categoryList = new ArrayList<String>();
		categoryList.add("arts_entertainment");
		categoryList.add("college_university");
		categoryList.add("food");
		categoryList.add("nightlife");
		categoryList.add("outdoors_recreation");
		categoryList.add("shop_service");
		categoryList.add("travel_transport");
		categoryList.add("residence");
		categoryList.add("other");
        
		for (String category : categoryList) {
			MapItemCategory c = new MapItemCategory();
			c.setName(category);
			mapItemCategoryService.add(c);
			
			message += "Added MapItemCategory: " + category + " <br />";
		}
				
		return message;
	}
	
	/**
	 * Import initial hotspots
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public String importInitialHotspots() throws JsonParseException, JsonMappingException, IOException {
				
		String message = "";
		//check if more than 50 hotspots
		if (hotspotService.list().size() > 50) {
			return "Already enough Hotspots" + " <br />";
		};
			
		RestTemplate restTemplate = new RestTemplate();
        
		String apiKey = messageSource.getMessage("google.api_key", null, null);
		
		List<Coordinate> coordinates =  new ArrayList<Coordinate>();
		coordinates.add(new Coordinate(48.195473, 16.369905)); //vienna
		coordinates.add(new Coordinate(47.075967, 15.440203)); //graz
		coordinates.add(new Coordinate(48.305121,14.289676)); //linz
		coordinates.add(new Coordinate(46.623329,14.308735)); //klagenfurt
		coordinates.add(new Coordinate(47.814999,13.054453)); //salzburg
		coordinates.add(new Coordinate(47.270319,11.404053)); //innsbruck
		
		List<String> types = new ArrayList<String>();
		types.add("bar");
		types.add("restaurant");
		types.add("museum");
		
		Integer radius = 10000;
		
		//get testuser
		User testUser = userService.getUserByEmail("importFrom@google.com");
		
		for (Coordinate coordinate : coordinates) {
					
		//call google places api for differnet types
			for (String type : types) {
				
				String json = restTemplate.getForObject(GOOGLE_PLACES_URL, 
						   String.class, String.valueOf(coordinate.getLatitude()), String.valueOf(coordinate.getLongitude()), String.valueOf(radius), type, apiKey);
				
				
				ObjectMapper mapper = new ObjectMapper();
				
				PlaceDetailsResponse response = mapper.readValue(json, PlaceDetailsResponse.class);
	
				List<PlaceDetails> result = response.getResults();
				
				for (PlaceDetails placeDetails : result) {
					
					Hotspot h = new Hotspot();
					
					Coordinate c = new Coordinate(Double.valueOf(placeDetails.getGeometry().getLocation().getLat()), 
												  Double.valueOf(placeDetails.getGeometry().getLocation().getLng()));
					coordinateService.add(c);
					
					h.setCoordinate(c);
					h.setTitle(placeDetails.getName());
					h.setDescription("This hotspot is awesome");
					h.setCost(1.0);
					
					List<MapItemCategory> categories = new ArrayList<MapItemCategory>();
					
					String categoryName = "";
					if (type.equals("bar"))  {
						categoryName = "nightlife";
					}
					else if (type.equals("restaurant"))  {
						categoryName = "food";
					}
					else if (type.equals("museum"))  {
						categoryName = "arts_entertainment";
					}
					
					MapItemCategory cat = mapItemCategoryService.getMapItemCategoryByName(categoryName);
					
					if (cat != null) {					
						categories.add(cat);					
						h.setCategories(categories);
					}
					
					if (testUser != null) {
						h.setUser(testUser);
					}
					
					hotspotService.add(h);
					
					message += "Added Hotspot: " + h.getTitle() + " <br />";				
					
				}
			}
		}
		return message;
	}
	
	public String importInitialRouteData() {
		
		String message = "";
		
		//SimpleGPXParser parser = new SimpleGPXParser();
		
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		List<Hotspot> hotspots = new ArrayList<Hotspot>();
		
		int counter = 1;
		
		User testUser = userService.find(2L);
		
		
		Boolean fileExists = true;
					
		while (fileExists) {
			
			File file = null;
			try {
				file = new ClassPathResource("dataimport/route_" + String.valueOf(counter) + ".gpx").getFile();
			}
			catch (Exception e) {
				fileExists = false;
			}
			
			if (fileExists) {
				try {
					coordinates = parser.parseGPXCoordinates(file);
					hotspots = parser.parseGPXHotspots(file);
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Route route = new Route("Testroute " + String.valueOf(counter), "How many roads");
				route.setCoordinate(coordinates.get(0));
				route.setCoordinates(coordinates);
				route.setHotspots(hotspots);
				route.setUser(testUser);
				routeService.add(route);
				
				
				
				message += "Added Route: " + route.getTitle() + " <br />";	
			}
			counter++;
		}
		
		return message;
	}
	
	
	/**
	 * Import initial EventCategories
	 * @return
	 */
	public String importInitialEventCategories(){
		String message = "";
		
		if(eventCategoryService.list().size() > 5) {
			return "Already enough EventCategories" + " <br />";
		}
		
		List<String> categoryList = new ArrayList<String>();
		categoryList.add("arts_entertainment");
		categoryList.add("eat_together");
		categoryList.add("shopping");
		categoryList.add("dancing");
		categoryList.add("party");
		categoryList.add("sightseeing");
		categoryList.add("other");
		
		for (String cat : categoryList) {
			EventCategory ec = new EventCategory();
			ec.setName(cat);
			eventCategoryService.add(ec);
			message += "Added EventCategory: " + cat + " <br />";
		}
		
		return message;
	}
	
	/**
	 * Import initial Events
	 * @return
	 */
	public String importInitialEvents(){
		String message = "";
		
		if(eventService.list().size() > 5){
			return "Already enough Events" + " <br />";
		}
		
		User testUser = userService.find(2L);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm");
		List<Date> testDates = new ArrayList<Date>();
		try {
			//one past date
			testDates.add(sdf.parse("10.01.2013T22:00"));
			//dates near mr3
			testDates.add(sdf.parse("28.01.2013T17:00"));
			testDates.add(sdf.parse("28.01.2013T18:00"));
			testDates.add(sdf.parse("28.01.2013T19:30"));
			testDates.add(sdf.parse("28.01.2014T20:45"));
			testDates.add(sdf.parse("28.01.2014T22:30"));
			testDates.add(sdf.parse("29.01.2014T09:45"));
			testDates.add(sdf.parse("29.01.2014T12:00"));
			testDates.add(sdf.parse("29.01.2014T14:30"));
			testDates.add(sdf.parse("29.01.2014T19:00"));
			testDates.add(sdf.parse("30.01.2013T13:00"));
			testDates.add(sdf.parse("30.01.2013T20:00"));
			testDates.add(sdf.parse("31.01.2014T16:30"));
			testDates.add(sdf.parse("31.01.2014T20:00"));
			testDates.add(sdf.parse("31.01.2013T21:00"));
			testDates.add(sdf.parse("31.01.2013T23:30"));
			//some future dates
			testDates.add(sdf.parse("05.02.2014T17:00"));
			testDates.add(sdf.parse("08.02.2014T10:00"));
			testDates.add(sdf.parse("12.02.2014T16:30"));
			testDates.add(sdf.parse("14.02.2014T20:00"));
			testDates.add(sdf.parse("15.02.2013T15:00"));
			testDates.add(sdf.parse("25.02.2013T16:00"));
		} catch (ParseException e11) {
		}
		
		List<Event> events = new ArrayList<Event>();
		
		events.add(addEvent("Awesome party", testDates.get(0), 1L, userService.getUserByEmail("ramon@tripitude.com"), 5L));
		events.add(addEvent("Dinner", testDates.get(1), 2L, userService.getUserByEmail("gerdsch@tripitude.com"), 2L));
		events.add(addEvent("Kabarett", testDates.get(2), 25L, userService.getUserByEmail("georg@tripitude.com"), 1L));
		events.add(addEvent("Dance and Party", testDates.get(3), 1L, userService.getUserByEmail("thomas@tripitude.com"), 4L));
		events.add(addEvent("Kino Tonight", testDates.get(4), 17L, userService.getUserByEmail("matthias@tripitude.com"), 1L));
		events.add(addEvent("Late dinner", testDates.get(5), 23L, userService.getUserByEmail("martin@tripitude.com"), 2L));
		events.add(addEvent("Brunch", testDates.get(6), 10L, userService.getUserByEmail("ramon@tripitude.com"), 2L));
		events.add(addEvent("Lunch", testDates.get(7), 14L, userService.getUserByEmail("gerdsch@tripitude.com"), 2L));
		events.add(addEvent("Coffee and Cake", testDates.get(8), 21L, userService.getUserByEmail("georg@tripitude.com"), 2L));
		events.add(addEvent("Beer and more", testDates.get(9), 18L, userService.getUserByEmail("matthias@tripitude.com"), 5L));
		events.add(addEvent("Museum", testDates.get(10), 42L, userService.getUserByEmail("thomas@tripitude.com"), 1L));
		events.add(addEvent("DnB and Dubstep", testDates.get(11), 3L, userService.getUserByEmail("martin@tripitude.com"), 4L));
		events.add(addEvent("Party hard", testDates.get(12), 8L, userService.getUserByEmail("romy@facebook.com"), 5L));
		events.add(addEvent("After MR3 party", testDates.get(13), 26L, userService.getUserByEmail("ramon@tripitude.com"), 5L));
		events.add(addEvent("Abshaken", testDates.get(14), 3L, userService.getUserByEmail("michi@gmx.net"), 4L));
		events.add(addEvent("Pokertourney", testDates.get(15), 12L, userService.getUserByEmail("sabine@hotmail.com"), 7L));
		events.add(addEvent("Meeting at Dinner", testDates.get(16), 37L, userService.getUserByEmail("martin@tripitude.com"), 7L));
		events.add(addEvent("Presentation", testDates.get(17), 39L, userService.getUserByEmail("thomas@tripitude.com"), 7L));
		events.add(addEvent("Schoenbrunn", testDates.get(18), 41L, userService.getUserByEmail("romy@facebook.com"), 6L));
		events.add(addEvent("Haus des Meeres", testDates.get(19), 46L, userService.getUserByEmail("michi@gmx.net"), 6L));
		events.add(addEvent("Cafe und Shoppen", testDates.get(20), 68L, userService.getUserByEmail("michi@gmx.net"), 3L));
		events.add(addEvent("Fussball schauen", testDates.get(21), 55L, userService.getUserByEmail("sabine@hotmail.com"), 7L));
		
		for(Event e : events){
			eventService.add(e);
			message += "Added Event: " + e.getName() + " <br />";
		}

		Event event = eventService.find(new Long(14));
		List<User> attenders = new ArrayList<User>();
		for (int i = 3; i <= 7; i++) {
			attenders.add(userService.find(new Long(i)));
		}
		event.setAttendingUsers(attenders);
		eventService.update(event);
		message += "Added a few attending Users for Event:"+ event.getName() + ": "+attenders.toString()+"<br />";	
	
		return message;
	}
	
	private Event addEvent(String name, Date date, Long mapitemID, User host, Long catID){
		List<EventCategory> cats = new ArrayList<EventCategory>();
		cats.add(eventCategoryService.find(new Long(catID)));
		Event e = new Event(name, date, hotspotService.find(new Long(mapitemID)), host);
		e.setCategories(cats);
		return e;
	}
	
	/**
	 * Import initial comments (one comment per hotspot).
	 * @return
	 */
	public String importInitialComments(){
		String message = "";
		
		if(commentService.list().size() > 5){
			return "Already enough comments" + " <br />";
		}
		
		User testUser = userService.find(2L);
		List<Hotspot> hotspots = hotspotService.list();
		List<Comment> comments = new ArrayList<Comment>();
		
		for(Hotspot h : hotspots){
			if(h.getCategories().get(0).getName().equals("arts_entertainment")){
				comments.add(new Comment("Fun activity!", h, testUser));
			}
			if(h.getCategories().get(0).getName().equals("food")){
				comments.add(new Comment("Delicious!", h, testUser));
			}
			if(h.getCategories().get(0).getName().equals("arts_entertainment")){
				comments.add(new Comment("Party hard!", h, testUser));
			}
		}
		
		for(Comment c : comments){
			commentService.add(c);
			message += c.getText() + " <br />";
		}
		
		return message;
	}
	
	/**
	 * Import initial historyItems
	 * @return
	 */
	public String importInitialHistoryItems(){
		String message = "";
		
		if(historyItemService.list().size() > 5){
			return "Already enough history Items" + " <br />";
		}
		
		User testUser = userService.find(2L);
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm");
		List<Date> testDates = new ArrayList<Date>();
		try {
			testDates.add(sdf.parse("12.12.2013T22:00"));
			testDates.add(sdf.parse("12.12.2013T15:00"));
			testDates.add(sdf.parse("12.12.2013T12:00"));
			testDates.add(sdf.parse("13.12.2013T23:55"));
			testDates.add(sdf.parse("01.01.2014T15:00"));
			testDates.add(sdf.parse("02.01.2014T12:45"));
			testDates.add(sdf.parse("02.01.2014T17:00"));
			testDates.add(sdf.parse("03.01.2014T10:00"));
			testDates.add(sdf.parse("03.01.2014T16:30"));
			testDates.add(sdf.parse("03.01.2014T20:00"));
		} catch (ParseException e11) {
		}
		
		
		MapItemSearchCriteria searchCriteria = new MapItemSearchCriteria();
		searchCriteria.setMaxResults(10);		
		searchCriteria.setBoundingCircleCriteria(new BoundingCircleCriteria(new Coordinate(48.195559, 16.367845), 5000.0));
		List<MapItem> mapItems = mapItemService.findByVariousCriteria(searchCriteria);
		
		int c = 0;
		for(MapItem m : mapItems){
			
			HistoryItem h = new HistoryItem(testDates.get(c++), m, testUser);
			historyItemService.add(h);
			message += "Added history item: " + m.getTitle() + " <br />";
		}
		
		return message;
	}
	
	
	
	//Classes for object mapping - I hate java for this
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PlaceDetailsResponse {
	    @JsonProperty("results")
	    private List<PlaceDetails> results;
	 
	    public List<PlaceDetails> getResults() {
	        return results;
	    }
	 
	    public void setResult(List<PlaceDetails> results) {
	        this.results = results;
	    }
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PlaceDetails {
	    @JsonProperty("name")
	    private String name;
	 
	    @JsonProperty("icon")
	    private String icon;
	 
	    @JsonProperty("url")
	    private String url;
	 
	    @JsonProperty("formatted_address")
	    private String address;
	 
	    @JsonProperty("geometry")
	    private PlaceGeometry geometry;
	 
	    @JsonProperty("photos")
	    private List<PlacePhoto> photos = Collections.emptyList();
	    
	    public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public PlaceGeometry getGeometry() {
			return geometry;
		}

		public void setGeometry(PlaceGeometry geometry) {
			this.geometry = geometry;
		}

		public List<PlacePhoto> getPhotos() {
			return photos;
		}

		public void setPhotos(List<PlacePhoto> photos) {
			this.photos = photos;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PlaceGeometry {
	    @JsonProperty("location")
	    private PlaceLocation location;
	 
	    public PlaceLocation getLocation() {
	        return location;
	    }
	 
	    public void setLocation(PlaceLocation location) {
	        this.location = location;
	    }
	}
	 
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PlaceLocation {
	    @JsonProperty("lat")
	    private String lat;
	 
	    @JsonProperty("lng")
	    private String lng;
	     
	    public String getLat() {
			return lat;
		}

		public void setLat(String lat) {
			this.lat = lat;
		}

		public String getLng() {
			return lng;
		}

		public void setLng(String lng) {
			this.lng = lng;
		}
	}

	@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
	public static class PlacePhoto {
	    @JsonProperty("photo_reference")
	    private String reference;
	 
	    public String getReference() {
	        return reference;
	    }
	 
	    public void setReference(String reference) {
	        this.reference = reference;
	    }
	}
		

}

