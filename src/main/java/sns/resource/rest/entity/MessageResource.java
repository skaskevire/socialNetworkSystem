package sns.resource.rest.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import sns.resource.rest.DateSerializer;

public class MessageResource {
	@JsonSerialize(using = DateSerializer.class)
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
}
