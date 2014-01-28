package ac.tuwien.ase08.tripitude.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Comment")
public class Comment implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
	
	@Column(name = "created", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name = "text", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String text;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "mapitem_id")
	@JsonIgnore
	private MapItem mapItem;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "diaryItem_id")
	@JsonIgnore
	private DiaryItem diaryItem;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name = "name", length = 100)
	@Size(max = 100)
	private String name;
	

	public Comment() {
		
	}

	public Comment(String text) {
		super();
		this.text = text;
	}

	public Comment(String text, MapItem mapitem, User user) {
		super();
		this.text = text;
		this.mapItem = mapitem;
		this.user = user;
	}
	
	public long getId() {
		return id;
	}
	
	
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public MapItem getMapItem() {
		return mapItem;
	}

	public void setMapItem(MapItem mapitem) {
		this.mapItem = mapitem;
	}
	
	public DiaryItem getDiaryItem() {
		return diaryItem;
	}

	public void setDiaryItem(DiaryItem diaryItem) {
		this.diaryItem = diaryItem;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
		
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", text=" + text + ", mapitem=" + mapItem
				+ ", user=" + user + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mapItem == null) ? 0 : mapItem.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		Comment other = (Comment) obj;
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
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
}
