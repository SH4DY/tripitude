package ac.tuwien.ase08.tripitude.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Repository;

import ac.tuwien.ase08.tripitude.dao.interfaces.IUserDAO;
import ac.tuwien.ase08.tripitude.entity.User;


@Repository
public class UserDAO extends HibernateDAO<User, Long> implements IUserDAO {
	
	public void add(User user) {
		//encrypt befor save
		user.setPassword(encryptPassword(user.getPassword()));

		super.add(user);
	}
	
	public String encryptPassword(String password) {

		if (password.length() > 0) {

			ShaPasswordEncoder encoder = new ShaPasswordEncoder();
			return encoder.encodePassword(password, "qsease08");
		} 
		else {

			return "";
		}
	} 
}
