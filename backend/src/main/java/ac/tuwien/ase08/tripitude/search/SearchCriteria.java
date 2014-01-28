package ac.tuwien.ase08.tripitude.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mvel2.Operator;

public class SearchCriteria {
	
	private List<Long> ids = new ArrayList<Long>();
	
	private Integer maxResults = Integer.MAX_VALUE;
	
    private int operator = Operator.AND;
    
    
	public List<Long> getIds() {
		return ids;
	}
	
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
	
	public void setIds(String ids) {
		this.ids = commaSeparatedStringToLongList(ids);
	}
	
	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}
	
	public void setOperator(String operator) {
		
		if (operator.equals("and")) {			
			this.operator = Operator.AND;
		}
		else if (operator.equals("or")) {			
			this.operator = Operator.OR;
		}
	}
	
	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		
		if (maxResults != null) {			
			this.maxResults = maxResults;
		}
	}

	protected List<Long> commaSeparatedStringToLongList(String string) {
		
		List<Long> longList = new ArrayList<Long>();
		
		if (string.length() <= 0) {
			return longList;
		}
		
		List<String> stringList = Arrays.asList(string.split(","));
		
		for (String stringItem : stringList) {				
			longList.add(Long.parseLong(stringItem));
		}
		
	    return longList;	
	}
	
	protected List<Double> commaSeparatedStringToDoubleList(String string) {
		
		List<Double> doubleList = new ArrayList<Double>();
		
		if (string.length() <= 0) {
			return doubleList;
		}
		
		List<String> stringList = Arrays.asList(string.split(","));
		
		for (String stringItem : stringList) {				
			doubleList.add(Double.parseDouble(stringItem));
		}
		
	    return doubleList;	
	}
	
    protected List<String> commaSeparatedStringToStringList(String string) {
		
    	if (string.length() <= 0) {
			return new ArrayList<String>();
		}
    	
		return new ArrayList<String>(Arrays.asList(string.split(",")));	
	}
}
