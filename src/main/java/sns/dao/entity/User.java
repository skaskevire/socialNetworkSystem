package sns.dao.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;

import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import sns.resource.rest.CustomDateDeserializer;

@NodeEntity
public class User {
	@Id
	@GeneratedValue
	String id;


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	private String name;
	
	@JsonDeserialize(using = CustomDateDeserializer.class)
	private Date bdate;
	private String city;
	private List<Message> messages;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getBdate() {
		return bdate;
	}
	public void setBdate(Date bdate) {
		this.bdate = bdate;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
}
