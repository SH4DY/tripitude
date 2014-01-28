package ac.tuwien.ase08.tripitude.service.interfaces;

import java.util.List;

import ac.tuwien.ase08.tripitude.entity.HistoryItem;
import ac.tuwien.ase08.tripitude.entity.User;

public interface IHistoryItemService extends IGenericService<HistoryItem, Long> {

	public List<HistoryItem> getHistoryByUser(User user);
}
