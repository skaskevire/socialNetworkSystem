package quote.resource.entity;

public class Quote {
	private String time;
	private String message;

	public Quote(String time, String message) {
		super();
		this.time = time;
		this.message = message;
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
