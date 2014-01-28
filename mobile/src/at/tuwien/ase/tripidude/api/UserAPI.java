package at.tuwien.ase.tripidude.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Base64;
import at.tuwien.ase.tripidude.api.vo.RestAuthNonceSessionObj;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.File;
import at.tuwien.ase.tripidude.models.User;
import at.tuwien.ase.tripidude.utils.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;

public class UserAPI extends TripitudeAPI {
    
	private static UserAPI instance;
	private static User currentUser;
	
    public static final String LOGIN_EMAIL = "LOGIN_EMAIL";
    public static final String LOGIN_HASHED_PASS = "LOGIN_HASHED_PASS";
	
	public static User getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(User currentUser) {
		UserAPI.currentUser = currentUser;
	}

	public static UserAPI getInstance() {
		if (instance != null)
			return instance;
		else 
			return new UserAPI();
	}
	
	private UserAPI() {
		super.getInstance();
	}
	
	/**
	 * Check user session and try auto login
	 * @return
	 */
	public boolean doCheckUserSession() {
		
		RestAuthNonceSessionObj rns = getNonceSessionRequest();
		
		if (rns != null) {			
			setRestSession(rns);
			return true;
		}
		return false;
	}
    
	/**
	 * Manual login
	 * @param email
	 * @param password
	 * @return
	 */
	public Boolean doManualLogin(String email, String password) {

		//encode password 
		password = encodeStringSH1(password);
        
		String nonce = "";
		
		try {			
			 nonce = getRestSession().getNonce();
		} catch (NullPointerException e) {
			return false;
		}
		
		String hashedPassNonce = encodeStringSH1(nonce + password);
		
		//generate login object
		RestAuthLoginObj loginObj = new RestAuthLoginObj();
		loginObj.setEmail(email);
		loginObj.setHashedPassNonce(hashedPassNonce);
		
		try {
			performLogin(loginObj);
		} catch (Exception e) {
			Log.error(this, e.getMessage());
		} 

		if (currentUser != null) {
			
			//save registered credentials
			SharedPreferences.Editor editor = App.preferences.edit();
			editor.putString(UserAPI.LOGIN_EMAIL, email);
			//save hashed password
			editor.putString(UserAPI.LOGIN_HASHED_PASS, password);
	    	editor.commit();
			
			return true;
		}
		
		return false;
	}
    
	public Boolean doAutoLogin() {
			
		String email = App.preferences.getString(UserAPI.LOGIN_EMAIL, "");
		String hashedPass = App.preferences.getString(UserAPI.LOGIN_HASHED_PASS, "");
		
		if (email.length() > 0 && hashedPass.length() > 0) {				
		
			//set nonce and session;
			doCheckUserSession();
			
			String nonce = "";
			
			try {			
				 nonce = getRestSession().getNonce();
			} catch (NullPointerException e) {
				
				return false;
			}
			
			
			if (nonce == null || nonce.length() <= 0) {
				return false;
			}
			
			String hashedPassNonce = encodeStringSH1(getRestSession().getNonce() + hashedPass);
			
			RestAuthLoginObj loginObj = new RestAuthLoginObj();
			loginObj.setEmail(email);
			loginObj.setHashedPassNonce(hashedPassNonce);
			
			User u = null;
			try {
				u = performLogin(loginObj);
			} catch (Exception e) {
				Log.error(this, e.getMessage());
				return false;
			}
			
			if (u != null) {
				return true;
			}	
		}
		
		return false;
	}
	/**
	 * Logout
	 */
	public void doLogout() {
		
		String url = BASE_URL + "user/logout";
		
		AjaxCallback<String> cb = getNewQuery();
		cb.url(url);
		
		RestErrorResponse errors = new RestErrorResponse();
		performQuery(cb, null, errors);
        
        currentUser = null;
	}
	/**
	 * Register user via API
	 * @param userName
	 * @param email
	 * @param password
	 * @return
	 * @throws APIException 
	 */
	public Boolean doRegisterUser(String userName, String email, String password) throws APIException  {
		
		String url = BASE_URL + "user/register";
		
		User newUser = new User();
		newUser.setName(userName);
		newUser.setEmail(email);
		newUser.setPassword(password);
		newUser.setPasswordConfirmation(password);
		
		//convert to JSON
		String newUserJson = null;

		try {
			newUserJson = objectMapper.writeValueAsString(newUser);
		} catch (Exception e) {
			throw new APIException(e.getMessage());
		} 

		//create POST BODY
		HttpEntity entity = null;
		try {
			entity = new StringEntity(newUserJson);
		} catch (UnsupportedEncodingException e) {
			throw new APIException(e.getMessage());
		}

		//add POST BODY
		Map<String, Object> params = new HashMap<String, Object>();
        params.put(AQuery.POST_ENTITY, entity);
        
        //cal API
        AjaxCallback<String> cb = getNewQuery();
		cb.url(url).params(params);
		
		RestErrorResponse errors = new RestErrorResponse();		
		User u = performQuery(cb, User.class, errors);
		
		//check errors
    	if (errors.getStatus() >= 400) {
			return false;
		}
        
    	//save registered credentials
		SharedPreferences.Editor editor = App.preferences.edit();
		editor.putString(UserAPI.LOGIN_EMAIL, email);
		//save hashed password
		editor.putString(UserAPI.LOGIN_HASHED_PASS, encodeStringSH1(password));
    	editor.commit();
    	
    	//set current user
		currentUser = u;

		return true;
	}
	/**
	 * Get none and seesion id via API
	 * @return
	 */
	private RestAuthNonceSessionObj getNonceSessionRequest() {
		
		String url = BASE_URL + "user/nonce";
		
		RestAuthNonceSessionObj rns = null;
		AjaxCallback<String> cb = getNewQuery();
		cb.url(url);
		
		RestErrorResponse errors = new RestErrorResponse();
		rns = performQuery(cb, RestAuthNonceSessionObj.class, errors);
        
		return rns;
	}
	
	/**
	 * Perform login via API
	 * @param loginObj
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private User performLogin(RestAuthLoginObj loginObj) throws JsonGenerationException, JsonMappingException, IOException {
		
		String url = BASE_URL + "user/login";
		
		currentUser = null; 
		//serialize object
		String loginObjJson = objectMapper.writeValueAsString(loginObj);

		HttpEntity entity =  new StringEntity(loginObjJson);

		Map<String, Object> params = new HashMap<String, Object>();
        params.put(AQuery.POST_ENTITY, entity);
        
        AjaxCallback<String> cb = getNewQuery();
		cb.url(url).params(params);
		
		RestErrorResponse errors = new RestErrorResponse();
		User u = performQuery(cb, User.class, errors);
        
		currentUser = u;
        
		return u;
	}
	/**
	 * Check if user is logged in
	 * @return
	 * @throws APIException 
	 */
	public Boolean isLoggedIn() throws APIException {
		
		String url = BASE_URL + "user/me";
		
		User u = get(url, User.class);
		
		if (u != null) {
			return true;
		}
		
		return false;
	}
	/**
	 * Encode string SH1
	 * @param string
	 * @return
	 */
	public static String encodeStringSH1(String string) {
	   ShaPasswordEncoder encoder = new ShaPasswordEncoder();
	   return encoder.encodePassword(string, "qsease08");
	}
	
 
	@Override
	public String getLogSourceName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * Rest Auth Login Object
	 * 
	 * @author dietl_ma
	 * 
	 */
	@SuppressWarnings("unused")
	private class RestAuthLoginObj {

		private String email;
		private String hashedPassNonce;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
		public String getHashedPassNonce() {
			return hashedPassNonce;
		}

		public void setHashedPassNonce(String hashedPassNonce) {
			this.hashedPassNonce = hashedPassNonce;
		}
	}
	
	public Boolean saveAvatar(Bitmap avatar) throws APIException {
		String url = BASE_URL + "avatar/save";
		Boolean success = true;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		avatar.compress(Bitmap.CompressFormat.JPEG, 90, bao);
		byte[] ba = bao.toByteArray();
		String encodedImage = Base64.encodeToString(ba, Base64.DEFAULT);
		File file =  post(url, encodedImage, File.class);
		if (file == null) {
			success = false;
		}
		return success;
	}
	
	public List<Event> getEvents() throws APIException {
		Event[] response = get(BASE_URL + "user/events", Event[].class);
		return Arrays.asList(response);
	}
	
	public User getUser() throws APIException {
		return get(BASE_URL + "user/" + currentUser.getId(), User.class);
	}

	public List<User> getHighscoreUsers() throws APIException {
		User[] response = get(BASE_URL + "user/highscores", User[].class);
		
		if (response == null || response.length == 0) return new ArrayList<User>();
		
		return Arrays.asList(response);
	}
}