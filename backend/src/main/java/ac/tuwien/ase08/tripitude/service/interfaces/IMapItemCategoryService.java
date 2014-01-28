package ac.tuwien.ase08.tripitude.service.interfaces;

import ac.tuwien.ase08.tripitude.entity.MapItemCategory;

public interface IMapItemCategoryService extends IGenericService<MapItemCategory, Long>{
	
	public MapItemCategory getMapItemCategoryByName(String name); 
}
