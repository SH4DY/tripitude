package ac.tuwien.ase08.tripitude.dao;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IRatingDAO;
import ac.tuwien.ase08.tripitude.entity.Rating;

@Repository
public class RatingDAO extends HibernateDAO<Rating, Long> implements IRatingDAO {

}
