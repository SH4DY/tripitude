package at.tuwien.ase.tripidude.models;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * MapItem is a Superclass for Hotspot and Route.
 * 
 */
public class MapItem {
	
    private Long id;
	
	private String title;
	
	private String description;
	
	private Coordinate coordinate;
	
	private List<File> pictures = new ArrayList<File>();
	
	private List<Event> events = new ArrayList<Event>();

	private List<Comment> comments = new ArrayList<Comment>();
	
	private List<Rating> ratings = new ArrayList<Rating>();
	
	private List<MapItemCategory> categories = new ArrayList<MapItemCategory>();
	
	private RatingCacheMapItem ratingCache;
	
	private User user;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	private String type;
	
	MapItem() {
		
	}

	public MapItem(String title, String description) {
		super();
		this.title = title;
		this.description = description;
	}	
	

	public MapItem(String title, String description,
			List<Event> events, List<MapItemCategory> categories, User user) {
		super();
		this.title = title;
		this.description = description;
		this.events = events;
		this.categories = categories;
		this.user = user;
	}
	
	protected void setId(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}


	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
    
	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	

	public List<File> getPictures() {
		return pictures;
	}

	public void setPictures(List<File> pictures) {
		this.pictures = pictures;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public List<MapItemCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<MapItemCategory> categories) {
		this.categories = categories;
	}
	
	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public RatingCacheMapItem getRatingCache() {
		return ratingCache;
	}
	public void setRatingCache(RatingCacheMapItem ratingCache) {
		this.ratingCache = ratingCache;
	}


	@Override
	public String toString() {
		// without events for readability
		return "MapItem [id=" + id + ", title=" + title + ", description="
				+ description + ", categories=" + ", coordinate=" + coordinate + categories + ", creator=" + user+"]";
	}
	
	@Override
	public int hashCode() {
		// without events, cause events are fetched lazily and so 2 objects wouldnt be equal before and after fetching
		// without categories, cause events are fetched lazily and so 2 objects wouldnt be equal before and after fetching
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// without events, cause events are fetched lazily and so 2 objects wouldnt be equal before and after fetching
		// without categories, cause events are fetched lazily and so 2 objects wouldnt be equal before and after fetching
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapItem other = (MapItem) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}	
}
