package ac.tuwien.ase08.tripitude.service.interfaces;

import java.util.List;

import ac.tuwien.ase08.tripitude.entity.Diary;
import ac.tuwien.ase08.tripitude.entity.User;

public interface IDiaryService extends IGenericService<Diary, Long> {
	public List<Diary> findByUser(User user);
	public Diary findFull(Long key);
	public String getAccessHash(Long key);
	public Boolean verifyAccessHash(Long key, String hashToVerify);
}
