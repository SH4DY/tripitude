package ac.tuwien.ase08.tripitude.dao;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IHotspotDAO;
import ac.tuwien.ase08.tripitude.entity.Hotspot;

@Repository
public class HotspotDAO extends HibernateDAO<Hotspot, Long> implements IHotspotDAO {
  
	public void add(Hotspot hotspot) {
		
		//be sure that all coordinates are initialized
        hotspot.getCoordinate().setRadians();
		super.add(hotspot);        
    }
	
}
