package ac.tuwien.ase08.tripitude.service.interfaces;

import java.util.List;

public interface IGenericService<E, K> {
  
	void add(E entity);
	
	void update(E entity);
	
	void remove(E entity);
	
	E find(K key);
    
	List<E> list();
}
