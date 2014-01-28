package ac.tuwien.ase08.tripitude.service.interfaces;

import ac.tuwien.ase08.tripitude.entity.EventCategory;

public interface IEventCategoryService extends IGenericService<EventCategory, Long> {
	
	public EventCategory getEventCategoryByName(String name); 

}
