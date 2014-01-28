package ac.tuwien.ase08.tripitude.dao;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IRatingCacheMapItemDAO;
import ac.tuwien.ase08.tripitude.entity.RatingCacheMapItem;

@Repository
public class RatingCacheMapItemDAO extends HibernateDAO<RatingCacheMapItem, Long> implements IRatingCacheMapItemDAO {

}
