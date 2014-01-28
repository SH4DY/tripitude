package ac.tuwien.ase08.tripitude.search;

import java.util.Date;

public class EventSearchCriteria extends MapItemSearchCriteria {

	private Date beginDate, endDate;
	
	public EventSearchCriteria() {
		super();
	}

	public EventSearchCriteria(MapItemSearchCriteria msc) {
		super(msc.getIds(), msc.getTypes(), msc.getCategoryIds(), msc.getTitlelike(), msc.getBoundingCircleCriteria(), msc.getMaxResults());
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "EventSearchCriteria [super="+super.toString()+"beginDate=" + beginDate + ", endDate="
				+ endDate + "]";
	}
}
