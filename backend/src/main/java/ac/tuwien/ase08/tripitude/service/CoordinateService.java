package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.ICoordinateDAO;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.service.interfaces.ICoordinateService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("coordinateService")
public class CoordinateService implements ICoordinateService {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private ICoordinateDAO coordinateDAO;
	
	public void add(Coordinate entity) {
		coordinateDAO.add(entity);		
	}

	public void update(Coordinate entity) {
		coordinateDAO.update(entity);		
	}

	public void remove(Coordinate entity) {
		coordinateDAO.remove(entity);		
	}

	public Coordinate find(Long key) {
		return coordinateDAO.find(key);
	}

	public List<Coordinate> list() {
		return coordinateDAO.list();
	}

}
