package ac.tuwien.ase08.tripitude.service.interfaces;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ac.tuwien.ase08.tripitude.entity.Event;
import ac.tuwien.ase08.tripitude.entity.User;

public interface IUserService extends IGenericService<User, Long> {
 
	User getUserByName(String name);
	
	User getUserByEmail(String email);
	
	User getCurrentUser();
	
	User registerUser(User user);
	
	void autoLogin(User user);
	
    User findWithMapItems(Long key);
    
    void logout(HttpServletRequest request, HttpServletResponse response);
    
	/**
	 * Adds achievement points for the user.
	 * @param u - The current user.
	 * @param points
	 */
    void addPoints(User u, Long points);
    
    List<Event> getEvents(Long key);

	List<User> highscoreList();
}
