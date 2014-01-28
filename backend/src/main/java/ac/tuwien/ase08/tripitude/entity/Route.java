package ac.tuwien.ase08.tripitude.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@DiscriminatorValue(value = "ROUTE")
public class Route extends MapItem implements Serializable {
	
	@OneToMany(fetch = FetchType.LAZY)
	@Cascade(CascadeType.ALL)
    @JoinTable(
            name="Route_Coordinate",
            joinColumns = @JoinColumn( name="route_id"),
            inverseJoinColumns = @JoinColumn( name="coordinate_id")
    )
	private List<Coordinate> coordinates = new ArrayList<Coordinate>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Route_Hotspot",
		joinColumns = @JoinColumn(name = "mapitem_id", nullable = false, updatable = false), 
		inverseJoinColumns = @JoinColumn(name = "hotspot_id", nullable = false, updatable = false))
	
	private List<Hotspot> hotspots = new ArrayList<Hotspot>();


	public Route() {
		super();
	}

	public Route(String title, String description) {
		super(title, description);
	}
	
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public List<Hotspot> getHotspots() {
		return hotspots;
	}

	public void setHotspots(List<Hotspot> hotspots) {
		this.hotspots = hotspots;
	}

	@Override
	public String toString() {
		return "Route ["+super.toString()+" coordinates=" + coordinates + ", hotspots=" + hotspots
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((coordinates == null) ? 0 : coordinates.hashCode());
		result = prime * result
				+ ((hotspots == null) ? 0 : hotspots.hashCode());
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
		Route other = (Route) obj;
		if (coordinates == null) {
			if (other.coordinates != null)
				return false;
		} else if (!coordinates.equals(other.coordinates))
			return false;
		if (hotspots == null) {
			if (other.hotspots != null)
				return false;
		} else if (!hotspots.equals(other.hotspots))
			return false;
		return true;
	}
	
		
	
	
}
