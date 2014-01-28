package at.tuwien.ase.tripidude.models;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Hotspot extends MapItem {
	
	private Double cost;
	
	private List<Route> routes = new ArrayList<Route>(); 
	
	@JsonIgnore
	private boolean visited;

	public Hotspot() {
		super();
	}
	
	public Hotspot(MapItem item) {
		super(item.getTitle(), item.getDescription(),
				item.getEvents(), item.getCategories(), item.getUser());
		setId(item.getId());
		setCoordinate(item.getCoordinate());
		setPictures(item.getPictures());
		setComments(item.getComments());
		setRatings(item.getRatings());
		setRatingCache(item.getRatingCache());
		setUser(item.getUser());
	}

	public Hotspot(String title, String description, Double cost) {
		super(title, description);
		setCost(cost);		
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
	
	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public Hotspot(String title, String description) {
		super(title, description);
		setCost(0.0);		
	}
	
	@Override
	public String toString() {
		return "Hotspot ["+super.toString()+ ", Cost=" + getCost() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getCost() == null) ? 0 : getCost().hashCode());
		
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
		Hotspot other = (Hotspot) obj;
		if (getCost() == null) {
			if (other.getCost() != null)
				return false;
		} else if (!getCost().equals(other.getCost()))
			return false;
		
		return true;
	}
		
}
