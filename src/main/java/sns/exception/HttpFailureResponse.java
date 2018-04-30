package sns.exception;

public class HttpFailureResponse {
	private final Integer code;
	private final String message;
	private final FailureType failureType;
	private final String exceptionClass;
	
	public HttpFailureResponse(Integer code, String message, String exceptionClass, FailureType failureType) {
		super();
		this.code = code;
		this.message = message;
		this.failureType = failureType;
		this.exceptionClass = exceptionClass;
	}

	public String getExceptionClass() {
		return exceptionClass;
	}

	enum FailureType {
		Business, Technical
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public FailureType getFailureType() {
		return failureType;
	}
	
}
