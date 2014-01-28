package ac.tuwien.ase08.tripitude.restauth;

public class RestAuthLoginObj {
	
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
