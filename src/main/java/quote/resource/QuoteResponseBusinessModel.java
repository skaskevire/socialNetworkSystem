package quote.resource;

public class QuoteResponseBusinessModel {
	public QuoteResponseBusinessModel(String processingType, String time, String message) {
		super();
		this.processingType = processingType;
		this.time = time;
		this.message = message;
	}

	private String processingType;
	private String time;
	private String message;
	
	public String getProcessingType() {
		return processingType;
	}

	public void setProcessingType(String processingType) {
		this.processingType = processingType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
