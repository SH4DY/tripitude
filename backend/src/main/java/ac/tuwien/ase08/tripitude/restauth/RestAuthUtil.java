package ac.tuwien.ase08.tripitude.restauth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.SessionScope;

import ac.tuwien.ase08.tripitude.entity.User;
import ac.tuwien.ase08.tripitude.service.interfaces.IUserService;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RestAuthUtil {
    
	private String nonce = null;
	
	@Autowired
	private IUserService userService;
	/**
	 * Generate nonce
	 */
	private void generateNonce() {		
		
		byte[] nonce_bytes = new byte[16];
		Random rand;
		try {
			rand = SecureRandom.getInstance("SHA1PRNG");
			rand.nextBytes(nonce_bytes);
			nonce = Long.toString(Math.abs(rand.nextLong()), 36);				
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}		
	}
	/**
	 * Returns current nonce
	 * @return
	 */
	public String getNonce() {
		
		if (nonce == null) {
		  generateNonce();
		}
		return nonce;
	}
	
	public String generateHash(String toHash) {
	
		ShaPasswordEncoder encoder = new ShaPasswordEncoder();
		return encoder.encodePassword(toHash, "qsease08");
	}
	/**
	 * Validates user login request
	 * @param loginObj
	 * @return
	 */
	public Boolean validateLoginRequest(RestAuthLoginObj loginObj) {
		
        User u = userService.getUserByEmail(loginObj.getEmail());
		
		if (u != null) {			
			//compare local hashed nonce and password with request
			String toCompare = generateHash(getNonce() + u.getPassword());
			
			if (loginObj.getHashedPassNonce().equals(toCompare)) {
				return true;
			}		
		}
		
		return false;
	}
}

