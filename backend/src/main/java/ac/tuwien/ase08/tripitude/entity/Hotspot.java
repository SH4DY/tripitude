package ac.tuwien.ase08.tripitude.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import javax.validation.constraints.NotNull;



@Entity
@DiscriminatorValue(value = "HOTSPOT")
public class Hotspot extends MapItem implements Serializable{
  			
	@Column(name = "Cost", nullable = true)
	private Double cost;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "hotspots")
	private List<Route> routes = new ArrayList<Route>();
	

	public Hotspot() {
		super();
	}

	public Hotspot(String title, String description, Double cost) {
		super(title, description);
		this.cost = cost;		
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

	
	public Hotspot(String title, String description) {
		super(title, description);
		this.cost = 0.0;		
	}
	
	@Override
	public String toString() {
		return "Hotspot ["+super.toString()+ ", Cost=" + cost + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cost == null) ? 0 : cost.hashCode());
		
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
		if (cost == null) {
			if (other.cost != null)
				return false;
		} else if (!cost.equals(other.cost))
			return false;
		
		return true;
	}
		
}
