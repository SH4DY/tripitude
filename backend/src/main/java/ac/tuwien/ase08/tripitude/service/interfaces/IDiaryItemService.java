package ac.tuwien.ase08.tripitude.service.interfaces;

import ac.tuwien.ase08.tripitude.entity.DiaryItem;

public interface IDiaryItemService extends IGenericService<DiaryItem, Long> {
	public DiaryItem findFull(Long key);
}
