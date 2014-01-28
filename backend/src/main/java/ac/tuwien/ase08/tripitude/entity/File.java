package ac.tuwien.ase08.tripitude.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "File")
public class File implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
	
	@Column(name = "location", nullable = false, length = 500)
	@Size(max=500)
	@NotNull
	private String location;
	
	@Column(name = "thumb_location", length = 500)
	@Size(max=500)
	private String thumbLocation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mapitem_id")
	@JsonIgnore
	private MapItem mapitem;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "diaryItem_id")
	@JsonIgnore
	private DiaryItem diaryItem;
	
	public File() {}

	public File(String location) {
		super();
		this.location = location;
	}

	public Long getId() {
		return id;
	}

	public String getLocation() {
		return location;
	}
	
	public String getThumbLocation() {
		return thumbLocation;
	}

	public void setThumbLocation(String thumbLocation) {
		this.thumbLocation = thumbLocation;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public MapItem getMapitem() {
		return mapitem;
	}

	public void setMapitem(MapItem mapitem) {
		this.mapitem = mapitem;
	}
	
	public DiaryItem getDiaryItem() {
		return diaryItem;
	}

	public void setDiaryItem(DiaryItem diaryItem) {
		this.diaryItem = diaryItem;
	}

	@Override
	public String toString() {
		return "File [id=" + id + ", location=" + location + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
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
		File other = (File) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		return true;
	}
}
