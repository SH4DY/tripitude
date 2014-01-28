package ac.tuwien.ase08.tripitude.service.interfaces;

import java.util.List;

import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.File;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.Rating;
import ac.tuwien.ase08.tripitude.search.EventSearchCriteria;
import ac.tuwien.ase08.tripitude.search.MapItemSearchCriteria;

public interface IMapItemService {
	public List<MapItem> findByVariousCriteria(MapItemSearchCriteria searchCriteria);
	
	public List<MapItem> findEventsByVariousCriteria(EventSearchCriteria searchCriteria);
	
	public MapItem find(Long id);
	
	public MapItem findFullMapItem(Long id);
	
	public List<Event> findEvents(Long id);

	public List<File> getPicturesOfMapItem(MapItem mapitem);
}
