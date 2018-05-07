package sns.exception;

public class NeededRetryException extends RuntimeException {
	private static final long serialVersionUID = 3826869052774360066L;

	public NeededRetryException(String message) {
		super(message);
	}
	
	public NeededRetryException(String message, Throwable e) {
		super(message, e);
	}
}
