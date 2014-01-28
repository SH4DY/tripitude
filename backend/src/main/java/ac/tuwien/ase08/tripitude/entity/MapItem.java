package ac.tuwien.ase08.tripitude.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;







import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * 
 * MapItem is a abstract Superclass for Hotspot and Route.
 * 
 */
@Entity
@Table(name = "MapItem")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="TYPE",
    discriminatorType=DiscriminatorType.STRING
)
//annotation prevents serialization loop caused by one-to-many many-to-on bidirectional realtion 
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class MapItem implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapitem_id")
    private Long id;
	
	@Column(name = "title", nullable = false, length = 100)
	@Size(max = 100)
	@NotNull
	private String title;
	
	@Column(name = "description", nullable = false, length = 50)
	@Size(max = 300)
	@NotNull
	private String description;
	
	@OneToOne(fetch = FetchType.EAGER)
	@Cascade(CascadeType.ALL)
    @JoinTable(
            name="MapItem_Coordinate",
            joinColumns = @JoinColumn( name="mapitem_id"),
            inverseJoinColumns = @JoinColumn( name="coordinate_id")
    )
	//@NotNull
	private Coordinate coordinate;
	

	@OneToMany(mappedBy = "mapItem", fetch = FetchType.LAZY)
	private List<Event> events = new ArrayList<Event>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mapItem")
	private List<Comment> comments = new ArrayList<Comment>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mapItem")
	private List<Rating> ratings = new ArrayList<Rating>();
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "MapItem_Category",
		joinColumns = @JoinColumn(name = "mapitem_id", nullable = false, updatable = false), 
		inverseJoinColumns = @JoinColumn(name = "category_id", nullable = false, updatable = false))
	private List<MapItemCategory> categories = new ArrayList<MapItemCategory>();
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "mapitem")
	@Fetch(value = FetchMode.SUBSELECT)
	@Cascade(CascadeType.DELETE)
	private List<File> pictures = new ArrayList<File>();
	
	@OneToOne(fetch = FetchType.EAGER, mappedBy = "mapItem")
	@Cascade(CascadeType.DELETE)
	private RatingCacheMapItem ratingCache;
	
	@Transient
	private List<Route> routes = new ArrayList<Route>();

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
	
	@Transient
	private Double cost;

	@Transient
	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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

	public Long getId() {
		return id;
	}
	
	//extra field for json serialization to distinguish between mapitem types
	@JsonProperty("type")
    public String getMapItemType() {
        return this.getClass().getName();
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

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public List<MapItemCategory> getCategories() {
		return categories;
	}

	public List<File> getPictures() {
		return pictures;
	}

	public void setPictures(List<File> pictures) {
		this.pictures = pictures;
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
	
	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public void setRatingCache(RatingCacheMapItem ratingCache) {
		this.ratingCache = ratingCache;
	}

	@Override
	public String toString() {
		// without events for readability
		return "MapItem [id=" + id + ", title=" + title + ", description="
				+ description + ", categories=" + categories + ", coordinate=" + coordinate + ", creator=" + user+"]";
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
