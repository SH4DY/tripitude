package ac.tuwien.ase08.tripitude.dao;

import java.util.Date;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IDiaryItemDAO;
import ac.tuwien.ase08.tripitude.entity.DiaryItem;
import ac.tuwien.ase08.tripitude.entity.User;

@Repository
public class DiaryItemDAO extends HibernateDAO<DiaryItem, Long> implements IDiaryItemDAO {
	
	public void add(DiaryItem diaryItem) {
		
		//add
		if (diaryItem.getTime() == null) {	
			
			if (diaryItem.getHistoryItem() != null) {
				diaryItem.setTime(diaryItem.getHistoryItem().getTime());				
			}
			else {
				
				diaryItem.setTime(new Date());
			}
		}

		super.add(diaryItem);
	}
}
