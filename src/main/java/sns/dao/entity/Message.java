package sns.dao.entity;

import java.util.Date;

public class Message implements Comparable<Message>{
	private Date date;
	private String message;
	private String user;

	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
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

	@Override
	public int compareTo(Message o) {
		int result = 0;
		if(date.getTime() >= o.getDate().getTime())
		{
			result = 1;
		}else
		{
			result = -1;
		}

		return result;
	}
}