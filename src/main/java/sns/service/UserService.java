package sns.service;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sns.dao.entity.Message;
import sns.dao.entity.User;
import sns.dao.mongo.MongoUserDao;
import sns.dao.neo4j.Neo4jUserDao;

@Component
public class UserService {
	@Autowired
	Neo4jUserDao neo4jUserDao;
	@Autowired
	MongoUserDao mongoUserDao;

	public void save(Exchange exchange) {
		User user = exchange.getIn().getBody(User.class);
		neo4jUserDao.saveUser(user);
		mongoUserDao.saveUser(user);
	}

	@SuppressWarnings("unchecked")
	public void addToFriends(Exchange exchange) {
		String name = (String) exchange.getIn().getHeader("username");
		String targetUser = (String) exchange.getIn().getHeader("targetUser");

		neo4jUserDao.addFriendshipRequestedRelation(name, targetUser);
	}

	public List<String> getInvitations(Exchange exchange) {
		String name = (String) exchange.getIn().getHeader("username");
		return neo4jUserDao.getIncomingFriendshipRequestedRelations(name);
	}

	@SuppressWarnings("unchecked")
	public void acceptInvitation(Exchange exchange) {
		String acceptor = (String) exchange.getIn().getHeader("username");
		String requestor = (String) ((LinkedHashMap<String, ?>) exchange.getIn().getBody()).get("requestor");
		neo4jUserDao.acceptInvitation(acceptor, requestor);
	}

	public List<String> exploreUsers(Exchange exchange) {
		String username = (String) exchange.getIn().getHeader("username");
		return neo4jUserDao.getNearestNodes(username);
	}

	public List<String> exploreNetwork(Exchange exchange) {
		String username = (String) exchange.getIn().getHeader("username");
		return neo4jUserDao.getAllConnectedNodes(username);
	}

	public void removeFriend(Exchange exchange) {
		String username = (String) exchange.getIn().getHeader("username");
		String friendToRemove = (String) exchange.getIn().getHeader("friendToRemove");
		neo4jUserDao.removeFriendRelation(username, friendToRemove);
	}

	public int distanceFactor(Exchange exchange) {
		String username = (String) exchange.getIn().getHeader("username");
		String targetUser = (String) exchange.getIn().getHeader("targetUser");
		return neo4jUserDao.distanceFactor(username, targetUser);
	}

	@SuppressWarnings("unchecked")
	public void postMessage(Exchange exchange) {
		String user = (String) exchange.getIn().getHeader("username");
		String message = (String) ((LinkedHashMap<String, ?>) exchange.getIn().getBody()).get("message");

		Message msg = new Message();
		msg.setDate(new Date());
		msg.setMessage(message);

		mongoUserDao.postMessage(user, msg);
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsers(Exchange exchange) {
		List<String> usernameList = ((List<String>) exchange.getIn().getBody());
		return mongoUserDao.getUsers(usernameList);
	}

	public List<User> filterUsers(Exchange exchange) throws ParseException {
		String name = (String) exchange.getIn().getHeader("name");
		String bdateRangeFloor = (String) exchange.getIn().getHeader("bdateRangeFloor");
		String bdateRangeCeiling = (String) exchange.getIn().getHeader("bdateRangeCeiling");
		String city = (String) exchange.getIn().getHeader("city");

		return mongoUserDao.find(name, bdateRangeFloor, bdateRangeCeiling, city);
	}
}
