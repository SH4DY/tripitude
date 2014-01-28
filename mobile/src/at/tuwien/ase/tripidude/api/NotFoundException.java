package at.tuwien.ase.tripidude.api;

public class NotFoundException extends APIException {

	private static final long serialVersionUID = 1L;

	public NotFoundException() {
		super();
	}

	public NotFoundException(String err) {
		super(err);
	}
}
