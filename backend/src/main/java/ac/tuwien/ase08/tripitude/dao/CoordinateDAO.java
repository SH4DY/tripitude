package ac.tuwien.ase08.tripitude.dao;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.ICoordinateDAO;
import ac.tuwien.ase08.tripitude.entity.Coordinate;

@Repository
public class CoordinateDAO extends HibernateDAO<Coordinate, Long> implements ICoordinateDAO {
  
	public void add(Coordinate coordinate) {

		//be sure that radian values are set
		if (coordinate.getLatitudeRad() == null || 
		    coordinate.getLongitudeRad() == null) {
			coordinate.setRadians();
		}
		
		super.add(coordinate);
	}
}
