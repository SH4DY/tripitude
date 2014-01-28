package at.tuwien.ase.tripidude.models;

public class File{

    private Long id;

	private String location;

	private String thumbLocation;

	private MapItem mapitem;

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
