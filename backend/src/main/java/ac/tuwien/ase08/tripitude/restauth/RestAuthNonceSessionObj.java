package ac.tuwien.ase08.tripitude.restauth;

public class RestAuthNonceSessionObj {
	
	private String nonce;
	
	private String sessionId;

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
}
