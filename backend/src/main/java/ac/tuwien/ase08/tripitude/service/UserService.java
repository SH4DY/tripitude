package ac.tuwien.ase08.tripitude.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ac.tuwien.ase08.tripitude.dao.interfaces.IUserDAO;
import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.MapItem;
import ac.tuwien.ase08.tripitude.entity.Role;
import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IRoleService;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service("userService")
public class UserService implements IUserService {
    
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    private IUserDAO userDAO;
	
	@Autowired
    private IRoleService roleService;
	
	@Autowired
	public UserDetailsManager userDetailsManager;
	
	public void add(User entity) {
		userDAO.add(entity);		
	}

	public void update(User entity) {
		userDAO.update(entity);		
	}

	public void remove(User entity) {
		userDAO.remove(entity);		
	}

	public User find(Long key) {
		//return userDAO.find(key);
		return (User) sessionFactory.getCurrentSession().get(User.class, key);
	}

	public List<User> list() {
		return userDAO.list();
	}

	@Override
	public User getUserByName(String name) {
		Query query = sessionFactory.getCurrentSession().createQuery("FROM User WHERE name= :name");
		query.setParameter("name", name);		
		List l = query.list();
		if (l.isEmpty()) {
			return null;
		}
		return (User) l.get(0);
	}
	
	@Override
	public User getUserByEmail(String email) {
		Query query = sessionFactory.getCurrentSession().createQuery("FROM User WHERE email= :email");
		query.setParameter("email", email);		
		List l = query.list();
		if (l.isEmpty()) {
			return null;
		}
		return (User) l.get(0);
	}
	

	@Override
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		User u = getUserByEmail(currentPrincipalName);
		return u;
	}

	@Override
	public User registerUser(User user) {
        Role role = roleService.getRoleByRole("AUTHENTICATED");	
        if (role != null) {        	
        	user.setRole(role);
        }
        user.setRank("Newbie");
        user.setPoints(0L);
        add(user);
		return user;
	}
    

	@Override
	public void autoLogin(User user) {
		UserDetails userDetails = userDetailsManager.loadUserByUsername(user.getEmail());		
		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Override
	public User findWithMapItems(Long key) {
		User u = userDAO.find(key);
		//initialize items
		Hibernate.initialize(u.getMapitems());
		return u;
	}

	public void logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextLogoutHandler ctxLogOut = new SecurityContextLogoutHandler();
		ctxLogOut.logout(request, response, auth);
	}

	@Override
	public void addPoints(User u, Long points){
		Long existingPoints = 0L;
		if (u.getPoints() != null) {
			existingPoints = u.getPoints();
		}
		 
		u.setPoints(existingPoints + points);
		u = setRank(u);
		update(u);
	}
	
	private User setRank(User user){
		if(user.getPoints() < 500)
			user.setRank("Newbie");
		else if(user.getPoints() >= 500 && user.getPoints() < 2500){
			user.setRank("Tourist");
		}
		else if(user.getPoints() >= 2500 && user.getPoints() < 5000){
			user.setRank("Traveler");
		}
		else if(user.getPoints() >= 5000 && user.getPoints() < 8000){
			user.setRank("Explorer");
		}
		else if(user.getPoints() >= 8000){
			user.setRank("Been there, done that.");
		}
		return user;
	}
	
	@Override
	public List<Event> getEvents(Long id) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"SELECT e FROM Event AS e LEFT JOIN e.attendingUsers AS au WHERE au.id = :id");
		query.setParameter("id", id);
		
		List<Event> l = (List<Event>)query.list();
		
		return l;
	}

	@Override
	public List<User> highscoreList() {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"SELECT u FROM User AS u ORDER BY u.points DESC");
				
		List<User> l = (List<User>)query.list();
		
		return l;
	}
	
}
