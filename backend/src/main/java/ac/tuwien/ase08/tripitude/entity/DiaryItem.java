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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "DiaryItem")
public class DiaryItem implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
	
	@Column(name = "name", length = 100)
	@Size(max=100)
	private String name;
	
	@Column(name = "text", length = 3000)
	@Size(max=3000)
	private String text;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name= "file_id")
	@Cascade(CascadeType.ALL)
	private File picture;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "historyitem_id")
	private HistoryItem historyItem;
	
	@Column(name = "time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date time;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "diary_id")
	@JsonIgnore
	private Diary diary;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "diaryItem")
	@Fetch(value = FetchMode.SUBSELECT)
	@Cascade(CascadeType.ALL)
	private List<Comment> comments = new ArrayList<Comment>();
	
	

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "diaryItem")
	@Fetch(value = FetchMode.SUBSELECT)
	@Cascade(CascadeType.ALL)
	private List<File> pictures = new ArrayList<File>();
	
	public DiaryItem() {}
	
	public DiaryItem(String name,
			HistoryItem historyItem, Diary diary) {
		super();
		this.name = name;
		this.historyItem = historyItem;
		this.diary = diary;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public File getPicture() {
		return picture;
	}

	public void setPicture(File picture) {
		this.picture = picture;
	}

	public HistoryItem getHistoryItem() {
		return historyItem;
	}

	public void setHistoryItem(HistoryItem historyItem) {
		this.historyItem = historyItem;
	}

	public Diary getDiary() {
		return diary;
	}

	public void setDiary(Diary diary) {
		this.diary = diary;
	}
	
	public List<File> getPictures() {
		return pictures;
	}

	public void setPictures(List<File> pictures) {
		this.pictures = pictures;
	}
	
	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "DiaryItem [id=" + id + ", name=" + name + ", text=" + text
				+ ", picture=" + picture + ", historyItem=" + historyItem
				+ ", diary=" + diary + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((diary == null) ? 0 : diary.hashCode());
		result = prime * result
				+ ((historyItem == null) ? 0 : historyItem.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((picture == null) ? 0 : picture.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		DiaryItem other = (DiaryItem) obj;
		if (diary == null) {
			if (other.diary != null)
				return false;
		} else if (!diary.equals(other.diary))
			return false;
		if (historyItem == null) {
			if (other.historyItem != null)
				return false;
		} else if (!historyItem.equals(other.historyItem))
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
		if (picture == null) {
			if (other.picture != null)
				return false;
		} else if (!picture.equals(other.picture))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
	
	
}
