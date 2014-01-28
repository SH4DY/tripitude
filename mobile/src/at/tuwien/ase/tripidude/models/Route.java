package at.tuwien.ase.tripidude.models;

import java.util.ArrayList;
import java.util.List;

public class Route extends MapItem {
	
	private List<Coordinate> coordinates = new ArrayList<Coordinate>();
	
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
