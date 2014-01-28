package ac.tuwien.ase08.tripitude.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue(value = "MAPITEM")
public class RatingCacheMapItem extends RatingCache{

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "mapitem_id")
	@NotNull
	@JsonIgnore
	private MapItem mapItem;

	public MapItem getMapItem() {
		return mapItem;
	}

	public void setMapItem(MapItem mapItem) {
		this.mapItem = mapItem;
	}

	@Override
	public String toString() {
		return "RatingCacheMapItem [mapItem=" + mapItem + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((mapItem == null) ? 0 : mapItem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RatingCacheMapItem other = (RatingCacheMapItem) obj;
		if (mapItem == null) {
			if (other.mapItem != null)
				return false;
		} else if (!mapItem.equals(other.mapItem))
			return false;
		return true;
	}

	
}
