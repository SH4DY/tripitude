package at.tuwien.ase.tripidude.models;

public class Coordinate {

    private Long id;

	private Double longitude;

	private Double latitude;

	private Double longitudeRad;

	private Double latitudeRad;

	public Coordinate() {}
	
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
