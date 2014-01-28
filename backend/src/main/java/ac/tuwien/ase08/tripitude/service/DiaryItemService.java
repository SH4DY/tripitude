package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.ICoordinateDAO;
import ac.tuwien.ase08.tripitude.dao.interfaces.IDiaryItemDAO;
import ac.tuwien.ase08.tripitude.entity.Coordinate;
import ac.tuwien.ase08.tripitude.entity.Diary;
import ac.tuwien.ase08.tripitude.entity.DiaryItem;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryItemService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("diaryItemService")
public class DiaryItemService implements IDiaryItemService {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private IDiaryItemDAO diaryItemDAO;
	
	public void add(DiaryItem entity) {
		diaryItemDAO.add(entity);
	}

	public void update(DiaryItem entity) {
		diaryItemDAO.update(entity);		
	}

	public void remove(DiaryItem entity) {
		diaryItemDAO.remove(entity);		
	}

	public DiaryItem find(Long key) {
		return diaryItemDAO.find(key);
	}

	public List<DiaryItem> list() {
		return diaryItemDAO.list();
	}
	
	public DiaryItem findFull(Long key) {
		
		DiaryItem d = diaryItemDAO.find(key);
		
		if (d != null) {
			Hibernate.initialize(d.getDiary());
			Hibernate.initialize(d.getComments());
		}
		
		return d;
	}

}
