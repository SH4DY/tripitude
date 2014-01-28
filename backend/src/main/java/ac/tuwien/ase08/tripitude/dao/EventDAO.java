package ac.tuwien.ase08.tripitude.dao;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IEventDAO;
import ac.tuwien.ase08.tripitude.entity.Event;

@Repository
public class EventDAO extends HibernateDAO<Event, Long> implements IEventDAO {

}
