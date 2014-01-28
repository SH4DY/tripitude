package ac.tuwien.ase08.tripitude.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "User")
//annotation prevents serialization loop caused by one-to-many many-to-on bidirectional realtion 
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class User implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
	
	@Column(name = "name", nullable = false, length = 30)
	@Size(min=5, max=30)
	@NotNull
	private String name;
	
	@Column(name = "password", nullable = false)
	@Size(min=6, max=50)
	@NotNull
	@JsonIgnore
	private String password;
	
	@Transient
	@JsonIgnore
	private String passwordConfirmation;
	
	@Column(name = "email", nullable = false, length = 40, unique = true)
	@Size(max = 40)
	@Email
	@NotNull
	private String email;
	
	@Column(name = "rank")
	private String rank;
	
	@Column(name = "points")
	private Long points = 0L;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name= "role_id")
	private Role role;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name= "file_id")
	private File picture;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<MapItem> mapitems = new ArrayList<MapItem>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<Comment> comments = new ArrayList<Comment>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<Rating> ratings = new ArrayList<Rating>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<Diary> diarys = new ArrayList<Diary>();
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "attendingUsers")
	private List<Event> attendingEvents = new ArrayList<Event>();
	
	public User() {
		
	}
	
	public User(String name, String password, String email) {
		super();
		this.name = name;
		this.password = password;
		this.email = email;
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
	
	//ignore on json serialization
	@JsonIgnore
	public String getPassword() {
		return password;
	}
    
	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}
	
	//ignore on json serialization
	@JsonIgnore	
	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}
	
	@JsonProperty
	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public File getPicture() {
		return picture;
	}

	public void setPicture(File picture) {
		this.picture = picture;
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

	public List<MapItem> getMapitems() {
		return mapitems;
	}

	public void setMapitems(List<MapItem> mapitems) {
		this.mapitems = mapitems;
	}

	public List<Diary> getDiarys() {
		return diarys;
	}

	public void setDiarys(List<Diary> diarys) {
		this.diarys = diarys;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", password=" + password
				+ ", email=" + email + ", points=" + points + ", role=" + role + ", picture="
				+ picture + "]";
	}

	public List<Event> getAttendingEvents() {
		return attendingEvents;
	}

	public void setAttendingEvents(List<Event> attendingEvents) {
		this.attendingEvents = attendingEvents;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((picture == null) ? 0 : picture.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
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
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (picture == null) {
			if (other.picture != null)
				return false;
		} else if (!picture.equals(other.picture))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}
}
