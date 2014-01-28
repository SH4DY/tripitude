package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.IDiaryDAO;
import ac.tuwien.ase08.tripitude.entity.Diary;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IDiaryService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("diaryService")
public class DiaryService implements IDiaryService {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private IDiaryDAO diaryDAO;
	
	private static final String HASH_NONCE = "diaryhash123";
	
	public void add(Diary entity) {
		diaryDAO.add(entity);		
	}

	public void update(Diary entity) {
		diaryDAO.update(entity);		
	}

	public void remove(Diary entity) {
		diaryDAO.remove(entity);		
	}

	public Diary find(Long key) {
		return diaryDAO.find(key);
	}

	public List<Diary> list() {
		return diaryDAO.list();
	}
	
	public List<Diary> findByUser(User user) {
		
		Query query = sessionFactory.getCurrentSession().createQuery(
				"SELECT d FROM Diary as d WHERE d.user=:user");
		query.setParameter("user", user);
		
		List<Diary> l = (List<Diary>)query.list();
		
		return l;
	}
	
	public Diary findFull(Long key) {
		
		Diary d = diaryDAO.find(key);
		
		if (d != null) {
			Hibernate.initialize(d.getDiaryItems());
		}
		
		return d;
	}
	
	public String getAccessHash(Long key) {
		
		Diary d = diaryDAO.find(key);
		
		if (d != null) {
			
			ShaPasswordEncoder encoder = new ShaPasswordEncoder();
			return encoder.encodePassword(d.getId().toString() + d.getUser().getId() + HASH_NONCE, "qsease08");
		}	
		return null;
	}
	
	public Boolean verifyAccessHash(Long key, String hashToVerify) {
		
		Diary d = diaryDAO.find(key);
		
		if (d != null) {
			
			ShaPasswordEncoder encoder = new ShaPasswordEncoder();
			String calculatedHash =  encoder.encodePassword(d.getId().toString() + d.getUser().getId() + HASH_NONCE, "qsease08");
			
			if (calculatedHash.equals(hashToVerify)) {
				return true;
			}
		}	
		return false;
	}
}
