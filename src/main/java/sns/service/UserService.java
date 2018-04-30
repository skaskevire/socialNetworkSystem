package sns.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sns.dao.entity.Message;
import sns.dao.entity.User;
import sns.dao.mongo.MongoUserDao;
import sns.dao.neo4j.Neo4jUserDao;
import sns.exception.BusinessException;

@Component
public class UserService {
	@Autowired
	Neo4jUserDao neo4jUserDao;
	@Autowired
	MongoUserDao mongoUserDao;

	public void generateUsersAndRelations(Exchange exchange) {
		Integer n = Integer.valueOf(getFieldFromExchangeHeader(exchange, "numberOfUsers")) + 5;
		System.out.println("Start!");
		List<User> users = new ArrayList<User>();
		Random r = new Random();
		for (int i = 0; i < n; i++) {
			User user = new User();
			user.setBdate(new Date());
			user.setCity("City" + r.nextInt(1000));
			user.setName("Name" + i + UUID.randomUUID().toString());
			users.add(user);

			// neo4jUserDao.saveUser(user);
			mongoUserDao.saveUser(user);

			try {
				Thread.sleep(1l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			Thread.sleep(10000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < n * 2; i++) {
			int firstUserIndex = r.nextInt(n - 2);
			int secondUserIndex = r.nextInt(n - 2);
			User user1 = users.get(firstUserIndex);
			User user2 = users.get(secondUserIndex);
			neo4jUserDao.addFriendshipRequestedRelation(user1.getName(), user2.getName());
			neo4jUserDao.acceptInvitation(user2.getName(), user1.getName());

		}

		for (int i = 0; i < n * 5; i++) {
			int nnn = r.nextInt(n - 2);

			User user1 = users.get(nnn);

			Message msg = new Message();
			msg.setDate(new Date());
			msg.setMessage(UUID.randomUUID().toString());

			mongoUserDao.postMessage(user1.getName(), msg);
		}
	}

	public void save(Exchange exchange) {
		User user = exchange.getIn().getBody(User.class);
		// neo4jUserDao.saveUser(user);
		mongoUserDao.saveUser(user);
	}

	public String getFieldFromExchangeHeader(Exchange exchange, String field) {
		return (String) exchange.getIn().getHeader(field);
	}

	@SuppressWarnings("unchecked")
	public String getFieldFromExchangeBody(Exchange exchange, String field) {
		return (String) ((LinkedHashMap<String, ?>) exchange.getIn().getBody()).get(field);
	}

	public void addToFriends(Exchange exchange) {
		Integer responseCode = neo4jUserDao.addFriendshipRequestedRelation(
				getFieldFromExchangeHeader(exchange, "username"), getFieldFromExchangeHeader(exchange, "targetUser"));
		if (responseCode == null) {
			throw new BusinessException("AddFriend operation failed: Invalid arguments");
		}
	}

	public List<String> getInvitations(Exchange exchange) {
		return neo4jUserDao.getIncomingFriendshipRequestedRelations(getFieldFromExchangeHeader(exchange, "username"));
	}

	public void acceptInvitation(Exchange exchange) {
		Integer responseCode = neo4jUserDao.acceptInvitation(getFieldFromExchangeHeader(exchange, "username"),
				getFieldFromExchangeHeader(exchange, "requestor"));
		if (responseCode == null) {
			throw new BusinessException("Accept operation failed: Invalid arguments");
		}

	}

	public List<String> exploreUsers(Exchange exchange) {
		return neo4jUserDao.getNearestNodes(getFieldFromExchangeHeader(exchange, "username"));
	}

	public List<Message> getAllFriendMessages(Exchange exchange) {
		return mongoUserDao.getUserMessages(exploreUsers(exchange));
	}

	public List<String> exploreNetwork(Exchange exchange) {
		return neo4jUserDao.getAllConnectedNodes(getFieldFromExchangeHeader(exchange, "username"));
	}

	public void removeFriend(Exchange exchange) {
		List<Integer> responseCodes = neo4jUserDao.removeFriendRelation(getFieldFromExchangeHeader(exchange, "username"),
				getFieldFromExchangeHeader(exchange, "friendToRemove"));
		if (responseCodes == null || responseCodes.size() != 2) {
			throw new BusinessException(
					"RemoveUser operation failed");
		}
	}

	public int distanceFactor(Exchange exchange) {
		Integer distanceFactor = neo4jUserDao.distanceFactor(getFieldFromExchangeHeader(exchange, "username"),
				getFieldFromExchangeHeader(exchange, "targetUser"));
		if (distanceFactor == null) {
			throw new BusinessException("DistanceFactor operation failed: sourceUser or targetUser name incorrect");
		}

		return distanceFactor;
	}

	public void postMessage(Exchange exchange) {
		Message msg = new Message();
		msg.setDate(new Date());
		msg.setMessage(getFieldFromExchangeBody(exchange, "message"));

		mongoUserDao.postMessage(getFieldFromExchangeHeader(exchange, "username"), msg);
	}

	public List<User> getUsers(Exchange exchange) {
		@SuppressWarnings("unchecked")
		List<String> usernameList = ((List<String>) exchange.getIn().getBody());
		return mongoUserDao.getUsers(usernameList);
	}

	public List<User> filterUsers(Exchange exchange) throws ParseException {
		String name = getFieldFromExchangeHeader(exchange, "name");
		String bdateRangeFloor = getFieldFromExchangeHeader(exchange, "bdateRangeFloor");
		String bdateRangeCeiling = getFieldFromExchangeHeader(exchange, "bdateRangeCeiling");
		String city = getFieldFromExchangeHeader(exchange, "city");

		return mongoUserDao.find(name, bdateRangeFloor, bdateRangeCeiling, city);
	}
}
