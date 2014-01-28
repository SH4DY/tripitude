package ac.tuwien.ase08.tripitude.dao.interfaces;

import java.util.List;
/**
 * Generic DAO
 * @author dietl_ma
 *
 * @param <E>
 * @param <K>
 */
public interface IGenericDAO<E, K> {
    
	void add(E entity);
	
	void update(E entity);
	
	void remove(E entity);
	
	E find(K key);
    
	List<E> list();
}
