package sns.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Component;

import sns.converter.Converter;
import sns.dao.entity.Message;
import sns.dao.entity.User;
import sns.dao.mongo.MongoUserDao;
import sns.dao.neo4j.Neo4jUserDao;
import sns.exception.BusinessException;
import sns.resource.rest.entity.MessageResource;
import sns.resource.rest.entity.UserResource;

@Component
public class UserService {
	@Autowired
	private Neo4jUserDao neo4jUserDao;
	@Autowired
	private MongoUserDao mongoUserDao;
	@Autowired
	@Qualifier("messageResourceConverter")
	private Converter<MessageResource, Message> messageResourceConverter;
	@Autowired
	@Qualifier("userResourceConverter")
	private Converter<UserResource, User> userResourceConverter;
	@Autowired
	@Qualifier("messageConverter")
	private Converter<Message, MessageResource> messageConverter;
	@Autowired
	@Qualifier("userConverter")
	private Converter<User, UserResource> userConverter;
	public void save(Exchange exchange) {
		UserResource user = exchange.getIn().getBody(UserResource.class);
		mongoUserDao.saveUser(userResourceConverter.convert(user));
	}
	
	public void delete(Exchange exchange) {
		String user = getFieldFromExchangeHeader(exchange, "username");
		mongoUserDao.removeUser(user);
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

	public List<MessageResource> getAllFriendMessages(Exchange exchange) {
		List<MessageResource> mrList = new ArrayList<MessageResource>();
		List<Message> mList = mongoUserDao.getUserMessages(exploreUsers(exchange));
		if(mList!=null)
		{
			for(Message message : mList)
			{
				mrList.add(messageConverter.convert(message));
			}
		}

		return mrList;
	}

	public List<MessageResource> getAllNetworkMessages(Exchange exchange) {
		List<MessageResource> mrList = new ArrayList<MessageResource>();
		List<Message> mList = mongoUserDao.getUserMessages(exploreNetwork(exchange));
		if(mList!=null)
		{
			for(Message message : mList)
			{
				mrList.add(messageConverter.convert(message));
			}
		}

		return mrList;
	}

	public List<String> exploreNetwork(Exchange exchange) {
		return neo4jUserDao.getAllConnectedNodes(getFieldFromExchangeHeader(exchange, "username"));
	}

	public void removeFriend(Exchange exchange) {
		List<Integer> responseCodes = neo4jUserDao.removeFriendRelation(
				getFieldFromExchangeHeader(exchange, "username"),
				getFieldFromExchangeHeader(exchange, "friendToRemove"));
		if (responseCodes == null || responseCodes.size() != 2) {
			throw new BusinessException("RemoveUser operation failed");
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

	public List<UserResource> getUsers(Exchange exchange) {
		@SuppressWarnings("unchecked")
		List<String> usernameList = ((List<String>) exchange.getIn().getBody());
		 List<User> userList = mongoUserDao.getUsers(usernameList);
		 List<UserResource> userResourceList = new ArrayList<UserResource>();
		 for(User user : userList)
		 {
			 userResourceList.add(userConverter.convert(user));
		 }
		return userResourceList;
	}

	public List<User> filterUsers(Exchange exchange) throws ParseException {
		String name = getFieldFromExchangeHeader(exchange, "name");
		String bdateRangeFloor = getFieldFromExchangeHeader(exchange, "bdateRangeFloor");
		String bdateRangeCeiling = getFieldFromExchangeHeader(exchange, "bdateRangeCeiling");
		String city = getFieldFromExchangeHeader(exchange, "city");

		return mongoUserDao.find(name, bdateRangeFloor, bdateRangeCeiling, city);
	}
	
	 public Long userCount() {
		  return neo4jUserDao.userCount();
		 }
}
