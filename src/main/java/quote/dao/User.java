package quote.dao;

import javax.persistence.GeneratedValue;

import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.annotation.Id;
@NodeEntity
public class User {
	@Id
	@GeneratedValue
	Long id;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
