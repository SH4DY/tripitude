package at.tuwien.ase.tripidude.models;

public class RatingCache {
	
	private Long id;

	private Integer numRatings;

	private Double sum;

	private Double average;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
    	
	public Integer getNumRatings() {
		return numRatings;
	}

	public void setNumRatings(Integer numRatings) {
		this.numRatings = numRatings;
	}

	public Double getSum() {
		return sum;
	}

	public void setSum(Double sum) {
		this.sum = sum;
	}

	public Double getAverage() {
		return average;
	}

	public void setAverage(Double average) {
		this.average = average;
	}

	@Override
	public String toString() {
		return "RatingCache [id=" + id + ", numRatings=" + numRatings
				+ ", sum=" + sum + ", average=" + average + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((average == null) ? 0 : average.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((numRatings == null) ? 0 : numRatings.hashCode());
		result = prime * result + ((sum == null) ? 0 : sum.hashCode());
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
		RatingCache other = (RatingCache) obj;
		if (average == null) {
			if (other.average != null)
				return false;
		} else if (!average.equals(other.average))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (numRatings == null) {
			if (other.numRatings != null)
				return false;
		} else if (!numRatings.equals(other.numRatings))
			return false;
		if (sum == null) {
			if (other.sum != null)
				return false;
		} else if (!sum.equals(other.sum))
			return false;
		return true;
	}
}