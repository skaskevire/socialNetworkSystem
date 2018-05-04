package sns.exception;

public class NotYetCreatedException extends RuntimeException {
	private static final long serialVersionUID = 3826869052774360066L;

	public NotYetCreatedException(String message) {
		super(message);
	}
}
