package at.tuwien.ase.tripidude.models;

import java.util.ArrayList;
import java.util.List;

public class Diary {

    private Long id;

	private String name;

	private String description;

	private File picture;

	private User user;

	private List<DiaryItem> diaryItems = new ArrayList<DiaryItem>();

	public Diary() {}

	public Diary(String name, User user) {
		super();
		this.name = name;
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
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public File getPicture() {
		return picture;
	}

	public void setPicture(File picture) {
		this.picture = picture;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<DiaryItem> getDiaryItems() {
		return diaryItems;
	}

	public void setDiaryItems(List<DiaryItem> diaryItems) {
		this.diaryItems = diaryItems;
	}

	@Override
	public String toString() {
		// without diaryitems for readability
		return "Diary [id=" + id + ", name=" + name + ", picture=" + picture
				+ ", user=" + user + "]";
	}

	@Override
	public int hashCode() {
		// without diaryitems, cause lazily fetched
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((picture == null) ? 0 : picture.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// without diaryitems, cause lazily fetched
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Diary other = (Diary) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (picture == null) {
			if (other.picture != null)
				return false;
		} else if (!picture.equals(other.picture))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
}