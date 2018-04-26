package sns.dao.entity;

import java.util.Date;

public class Message {
	private Date date;
	private String message;

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}