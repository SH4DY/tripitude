package ac.tuwien.ase08.tripitude.util;

import org.springframework.stereotype.Component;

@Component
public class CoordinateUtils {
	
	public Double getRadianByDegree(Double degree) {
		
		return degree * Math.PI/180;
	}
}
