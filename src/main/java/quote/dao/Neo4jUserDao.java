package quote.dao;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Neo4jUserDao {
	@Autowired
	UserRepository repository;

	public void saveUser(Exchange exchange) {
		quote.resource.entity.User userIn = exchange.getIn().getBody(quote.resource.entity.User.class);

		User user = new User();
		user.setName(userIn.getName());

		repository.save(user);
	}
}
