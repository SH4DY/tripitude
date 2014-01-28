package ac.tuwien.ase08.tripitude.search;

import java.util.ArrayList;
import java.util.List;

import ac.tuwien.ase08.tripitude.entity.Coordinate;


public class MapItemSearchCriteria extends SearchCriteria{
	
	private List<String> types = new ArrayList<String>();
	
	private String titlelike = "";
	
	private List<Long> categoryIds = new ArrayList<Long>();
	
	private BoundingCircleCriteria boundingCircleCriteria = null;
	
	private Boolean orderByRating = false;
	
	public MapItemSearchCriteria() {}
	
	public MapItemSearchCriteria(List<Long> ids, List<String> types2,
			List<Long> categoryIds2, String titlelike2,
			BoundingCircleCriteria boundingCircleCriteria2, Integer maxResults) {
		this.setIds(ids);
		this.setTypes(types2);
		this.setCategoryIds(categoryIds2);
		this.setTitlelike(titlelike2);
		this.setBoundingCircleCriteria(boundingCircleCriteria2);
		this.setMaxResults(maxResults);
	}

	public String getTitlelike() {
		return titlelike;
	}
	
	public void setTitlelike(String titlelike) {
		this.titlelike = titlelike;
	}

	public List<Long> getCategoryIds() {
		return categoryIds;
	}
	
	public void setCategoryIds(List<Long> categoryIds) {
		this.categoryIds = categoryIds;
	}
	
	public void setCategoryIds(String categoryIds) {
		this.categoryIds = commaSeparatedStringToLongList(categoryIds);
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}
	
	public void setTypes(String types) {
		this.types = commaSeparatedStringToStringList(types);
	}	
	
	public BoundingCircleCriteria getBoundingCircleCriteria() {
		return boundingCircleCriteria;
	}

	public void setBoundingCircleCriteria(BoundingCircleCriteria boundingCircleCriteriaString) {
		this.boundingCircleCriteria = boundingCircleCriteriaString;
	}
	
	public void setBoundingCircleCriteria(String boundingCircleCriteriaString) {
		
		List<Double> argList = commaSeparatedStringToDoubleList(boundingCircleCriteriaString);
		
		if (argList.size() == 3) {
			
			Coordinate center = new Coordinate(argList.get(0), argList.get(1));
			this.boundingCircleCriteria = new BoundingCircleCriteria(center, argList.get(2));
		}	
	}

	public Boolean getOrderByRating() {
		return orderByRating;
	}
	
	public void setOrderByRating(Boolean orderByRating) {
		this.orderByRating = orderByRating;
	}

	@Override
	public String toString() {
		return "MapItemSearchCriteria [types=" + types + ", titlelike="
				+ titlelike + ", categoryIds=" + categoryIds
				+ ", boundingCircleCriteria=" + boundingCircleCriteria
				+ ", orderByRating=" + orderByRating + "]";
	}
}

