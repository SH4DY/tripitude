package ac.tuwien.ase08.tripitude.service.interfaces;

import java.util.List;

import ac.tuwien.ase08.tripitude.entity.Hotspot;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.search.MapItemSearchCriteria;


public interface IHotspotService extends IGenericService<Hotspot, Long> {
  
    public List<Hotspot> findByIds(List<Long> ids);
	
	public List<Hotspot> findByMapItemCategoryIds(List<Long> categoryIds);
	
	public List<Hotspot> findByTitleLike(String titleLike);
	
	public List<MapItem> findByVariousCriteria(MapItemSearchCriteria searchCriteria);
	
	public Hotspot findFullHotspot(Long key);
}
