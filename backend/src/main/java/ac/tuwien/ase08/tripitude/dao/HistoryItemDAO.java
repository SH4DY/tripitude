package ac.tuwien.ase08.tripitude.dao;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IHistoryItemDAO;
import ac.tuwien.ase08.tripitude.entity.HistoryItem;

@Repository
public class HistoryItemDAO extends HibernateDAO<HistoryItem, Long> implements IHistoryItemDAO {

}
