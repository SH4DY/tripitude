package ac.tuwien.ase08.tripitude.dao;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IEventCategoryDAO;
import ac.tuwien.ase08.tripitude.entity.EventCategory;

@Repository
public class EventCategoryDAO extends HibernateDAO<EventCategory, Long> implements IEventCategoryDAO {

}
