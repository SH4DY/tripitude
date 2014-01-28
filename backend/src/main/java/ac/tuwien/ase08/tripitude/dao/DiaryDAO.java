package ac.tuwien.ase08.tripitude.dao;

import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IDiaryDAO;
import ac.tuwien.ase08.tripitude.entity.Diary;

@Repository
public class DiaryDAO extends HibernateDAO<Diary, Long> implements IDiaryDAO {

}
