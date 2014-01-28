package ac.tuwien.ase08.tripitude.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Coordinate")
public class Coordinate implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
	
	//X Coordinate
	@Column(name = "longitude", nullable = false)
	@NotNull
	private Double longitude;
	
	//Y Coordinate
	@Column(name = "latitude", nullable = false)
	@NotNull
	private Double latitude;
	
	//X Coordinate
	@Column(name = "longitude_rad", nullable = false)
	private Double longitudeRad;
	
	//Y Coordinate
	@Column(name = "latitude_rad", nullable = false)
	private Double latitudeRad;
	
//
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "route_id")
//	private Route route;


	public Coordinate() {
		
	}
	
	public Coordinate(Double latitude, Double longitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		
		this.setRadians();
	}
	
	

	public Long getId() {
		return id;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitudeRad() {
		return longitudeRad;
	}
	
	public void setLongitudeRad(Double longitudeRad) {
		this.longitudeRad = longitudeRad;
	}
	
	public Double getLatitudeRad() {
		return latitudeRad;
	}
	
	public void setLatitudeRad(Double latitudeRad) {
		this.latitudeRad = latitudeRad;
	}
	
	public void setRadians() {
		//set rad values
		
		if (this.longitude != null && this.latitude != null) {
			
			this.longitudeRad =  this.longitude * Math.PI/180;
			this.latitudeRad =  this.latitude * Math.PI/180;
		}
	}

	@Override
	public String toString() {
		return "Coordinate [id=" + id + ", longitude=" + longitude
				+ ", latitude=" + latitude + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result
				+ ((longitude == null) ? 0 : longitude.hashCode());
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
		Coordinate other = (Coordinate) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		return true;
	}	
	
	
}
