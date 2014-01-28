package ac.tuwien.ase08.tripitude.dao;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IMapItemCategoryDAO;
import ac.tuwien.ase08.tripitude.entity.MapItemCategory;

@Repository
public class MapItemCategoryDAO extends HibernateDAO<MapItemCategory, Long> implements IMapItemCategoryDAO {
	
}
