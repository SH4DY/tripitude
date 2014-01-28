package ac.tuwien.ase08.tripitude.service.interfaces;

import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.Rating;
import ac.tuwien.ase08.tripitude.entity.User;

public interface IRatingService extends IGenericService<Rating, Long> {
	public Rating findByUserAndMapItem(User user, MapItem mapItem);
}
