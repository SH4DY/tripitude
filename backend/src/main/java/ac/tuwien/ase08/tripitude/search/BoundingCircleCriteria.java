package ac.tuwien.ase08.tripitude.search;

import ac.tuwien.ase08.tripitude.entity.Coordinate;

public class BoundingCircleCriteria {
	
	private Coordinate center;
	
	private Double radius;
	
	public BoundingCircleCriteria(Coordinate center, Double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Coordinate getCenter() {
		return center;
	}

	public void setCenter(Coordinate center) {
		this.center = center;
	}

	public Double getRadius() {
		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;
	}
}
