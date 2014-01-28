package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.IHistoryItemDAO;
import ac.tuwien.ase08.tripitude.entity.HistoryItem;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IHistoryItemService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("historyItemService")
public class HistoryItemService implements IHistoryItemService {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private IHistoryItemDAO historyItemDAO;
	
	public void add(HistoryItem entity) {
		historyItemDAO.add(entity);		
	}

	public void update(HistoryItem entity) {
		historyItemDAO.update(entity);		
	}

	public void remove(HistoryItem entity) {
		historyItemDAO.remove(entity);		
	}

	public HistoryItem find(Long key) {
		return historyItemDAO.find(key);
	}

	public List<HistoryItem> list() {
		return historyItemDAO.list();
	}
	
	public List<HistoryItem> getHistoryByUser(User user) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"SELECT h FROM HistoryItem as h WHERE h.user=:userid ORDER BY h.time DESC");
		query.setParameter("userid", user);
		
		List<HistoryItem> found = (List<HistoryItem>)query.list();
		
		return found;
	}

}
