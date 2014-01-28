package ac.tuwien.ase08.tripitude.service.interfaces;

import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.RatingCacheMapItem;

public interface IRatingCacheMapItemService extends IGenericService<RatingCacheMapItem, Long>{
	public RatingCacheMapItem findByMapItem(MapItem mapItem);
}
