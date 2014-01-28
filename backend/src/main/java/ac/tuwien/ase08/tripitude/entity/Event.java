package ac.tuwien.ase08.tripitude.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "Event")
public class Event implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
	
	@Column(name = "name", nullable = false, length = 50)
	@Size(min=5, max=50)
	@NotNull
	private String name;
	
	@Column(name = "time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date time;
	
	@Column(name = "description", length = 200)
	@Size(min=0, max=200)
	private String description;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Event_Category",
		joinColumns = @JoinColumn(name = "event_id", nullable = false, updatable = false), 
		inverseJoinColumns = @JoinColumn(name = "category_id", nullable = false, updatable = false))
	private List<EventCategory> categories = new ArrayList<EventCategory>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Event_User",
		joinColumns = @JoinColumn(name = "event_id", nullable = false, updatable = false), 
		inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false, updatable = false))
	private List<User> attendingUsers = new ArrayList<User>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "mapitem_id")
	private MapItem mapItem;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	@ForeignKey(name ="FK_user")
	private User user;

	public Event() {}
	
	public Event(String name, Date time, MapItem mapItem){
		super();
		this.name = name;
		this.time = time;
		this.mapItem = mapItem;
	}
	
	public Event(String name, Date time,
			MapItem mapItem, User user) {
		super();
		this.name = name;
		this.time = time;
		this.mapItem = mapItem;
		this.user = user;
	}

	public Event(String name, Date time, String description, MapItem mapItem,
			User user) {
		super();
		this.name = name;
		this.time = time;
		this.description = description;
		this.mapItem = mapItem;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<EventCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<EventCategory> categories) {
		this.categories = categories;
	}

	public MapItem getMapItem() {
		return mapItem;
	}

	public void setMapItem(MapItem mapItem) {
		this.mapItem = mapItem;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<User> getAttendingUsers() {
		return attendingUsers;
	}

	public void setAttendingUsers(List<User> attendingUsers) {
		this.attendingUsers = attendingUsers;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", name=" + name + ", time=" + time
				+ ", description=" + description + ", categories=" + categories
				+ ", attendingUsers=" + attendingUsers + ", mapItem=" + mapItem
				+ ", user=" + user + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mapItem == null) ? 0 : mapItem.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
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
		if (mapItem == null) {
			if (other.mapItem != null)
				return false;
		} else if (!mapItem.equals(other.mapItem))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}	
}
