package at.tuwien.ase.tripidude.models;

import java.util.ArrayList;
import java.util.List;

public class MapItemCategory {

    private Long id;

	private String name;

	private List<MapItem> mapItems = new ArrayList<MapItem>();
	
	public MapItemCategory() {}

	public MapItemCategory(String name) {
		super();
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MapItem> getMapItems() {
		return mapItems;
	}

	public void setMapItems(List<MapItem> mapItems) {
		this.mapItems = mapItems;
	}

	@Override
	public String toString() {
		return "MapItemCategory [id=" + id + ", name=" + name + ", mapItems="
				+ mapItems + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((mapItems == null) ? 0 : mapItems.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		MapItemCategory other = (MapItemCategory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (mapItems == null) {
			if (other.mapItems != null)
				return false;
		} else if (!mapItems.equals(other.mapItems))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}