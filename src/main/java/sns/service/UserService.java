package sns.service;

import java.net.URL;
import java.text.ParseException;
import java.util.*;

import org.apache.camel.Exchange;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Component;

import sns.cache.entity.DistanceFactorCalculationRequest;
import sns.converter.Converter;
import sns.dao.entity.Message;
import sns.dao.entity.User;
import sns.dao.entity.UserStatusEnum;
import sns.dao.mongo.MongoUserDao;
import sns.dao.neo4j.Neo4jUserDao;
import sns.exception.BusinessException;
import sns.exception.ExceptionProcessor;
import sns.exception.NeededRetryException;
import sns.resource.rest.entity.MessageResource;
import sns.resource.rest.entity.UserResource;

@Component
public class UserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
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

	@Autowired
	private Cache<String, DistanceFactorCalculationRequest> distanceFactorCalculationRequestsCache;
	public void save(Exchange exchange) {
		UserResource userResource = exchange.getIn().getBody(UserResource.class);
		User user = userResourceConverter.convert(userResource);
		user.setStatus(UserStatusEnum.pendingCreation.name());
		mongoUserDao.saveUser(user);
		exchange.getOut().setBody(user.getName());
	}

	public void markAsDeleted(Exchange exchange) {
		String user = getFieldFromExchangeHeader(exchange, "username");
		validateUser(user);
		exchange.getOut().setBody(user);
		mongoUserDao.setUserStatusPendingRemoval(user);
	}

	public void deleteFromMongo(Exchange exchange) {
		try {
			String user = exchange.getIn().getBody(String.class);
			mongoUserDao.removeUser(user);
			exchange.getOut().setBody(user);
		} catch (Exception e) {
			LOGGER.error(e.getMessage() + " Retrying...", e);
			throw new NeededRetryException(e.getMessage() + " Retrying...", e);
		}
	}

	public void deleteFromNeo4j(Exchange exchange) {
		try {
			String user = exchange.getIn().getBody(String.class);
			Integer response = neo4jUserDao.deleteUser(user);
			if (response == null) {
				throw new NeededRetryException(
						"User " + user + "not yet removed from Neo4j. Invalid response from DB");
			}
			exchange.getOut().setBody(user);
		} catch (Exception e) {
			LOGGER.error(e.getMessage() + " Retrying...", e);
			throw new NeededRetryException(e.getMessage() + " Retrying...", e);
		}
	}

	public String getFieldFromExchangeHeader(Exchange exchange, String field) {
		return (String) exchange.getIn().getHeader(field);
	}

	@SuppressWarnings("unchecked")
	public String getFieldFromExchangeBody(Exchange exchange, String field) {
		return (String) ((LinkedHashMap<String, ?>) exchange.getIn().getBody()).get(field);
	}

	public void addToFriends(Exchange exchange) {
		String username = getFieldFromExchangeHeader(exchange, "username");
		String targetUserName = getFieldFromExchangeHeader(exchange, "targetUser");

		validateUser(username);
		validateUser(targetUserName);

		Integer responseCode = neo4jUserDao.addFriendshipRequestedRelation(username, targetUserName);
		if (responseCode == null) {
			throw new BusinessException("AddFriend operation failed");
		}
	}

	public List<String> getInvitations(Exchange exchange) {
		String username = getFieldFromExchangeHeader(exchange, "username");

		validateUser(username);

		return neo4jUserDao.getIncomingFriendshipRequestedRelations(getFieldFromExchangeHeader(exchange, "username"));
	}

	public void acceptInvitation(Exchange exchange) {
		String username = getFieldFromExchangeHeader(exchange, "username");
		String requestorName = getFieldFromExchangeHeader(exchange, "requestor");

		validateUser(username);
		validateUser(requestorName);

		Integer responseCode = neo4jUserDao.acceptInvitation(username, requestorName);
		if (responseCode == null) {
			throw new BusinessException("Accept operation failed: Invalid arguments");
		}
	}

	private void validateUser(String username) {
		User user = mongoUserDao.getUser(username);
		if (user == null) {
			throw new BusinessException("User " + username + " is not exists!");
		}
		if (UserStatusEnum.pendingCreation.name().equals(user.getStatus())) {
			throw new BusinessException("User " + username + " not yet created, try later");
		}
		if (UserStatusEnum.pendingRemoval.name().equals(user.getStatus())) {
			throw new BusinessException("User " + username + " is removing now.");
		}
	}

	public List<String> exploreUsers(Exchange exchange) {
		String username = getFieldFromExchangeHeader(exchange, "username");
		validateUser(username);
		return neo4jUserDao.getNearestNodes(username);
	}

	public List<MessageResource> getAllFriendMessages(Exchange exchange) {
		List<MessageResource> mrList = new ArrayList<MessageResource>();
		List<Message> mList = mongoUserDao.getUserMessages(exploreUsers(exchange));
		if (mList != null) {
			for (Message message : mList) {
				mrList.add(messageConverter.convert(message));
			}
		}

		return mrList;
	}

	public List<MessageResource> getAllNetworkMessages(Exchange exchange) {
		List<MessageResource> mrList = new ArrayList<MessageResource>();
		List<Message> mList = mongoUserDao.getUserMessages(exploreNetwork(exchange));
		if (mList != null) {
			for (Message message : mList) {
				mrList.add(messageConverter.convert(message));
			}
		}

		return mrList;
	}

	public List<String> exploreNetwork(Exchange exchange) {
		String username = getFieldFromExchangeHeader(exchange, "username");

		validateUser(username);

		return neo4jUserDao.getAllConnectedNodes(username);
	}

	public void removeFriend(Exchange exchange) {
		String username = getFieldFromExchangeHeader(exchange, "username");
		String friendToRemove = getFieldFromExchangeHeader(exchange, "friendToRemove");

		validateUser(username);
		validateUser(friendToRemove);

		List<Integer> responseCodes = neo4jUserDao.removeFriendRelation(username, friendToRemove);
		if (responseCodes == null || responseCodes.size() != 2) {
			throw new BusinessException("RemoveUser operation failed");
		}
	}

	public void distanceFactor(Exchange exchange) {
		try {
			Thread.sleep(10000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Done!");
		String requestID = exchange.getIn().getBody(String.class);
		DistanceFactorCalculationRequest dfcr = distanceFactorCalculationRequestsCache.get(requestID);

		Integer distanceFactor = neo4jUserDao.distanceFactor(dfcr.getSourceUser(), dfcr.getTargetUser());
		if (distanceFactor == null) {
			throw new BusinessException("DistanceFactor operation failed: sourceUser or targetUser name incorrect");
		}

		dfcr.setStatus("calculationCompleted");
		dfcr.setDistanceFactor(distanceFactor);
	}

	public void returnDistanceFactor(Exchange exchange)
	{
		String requestID = getFieldFromExchangeHeader(exchange, "requestID");

		DistanceFactorCalculationRequest dfcr = distanceFactorCalculationRequestsCache.get(requestID);
		if(dfcr == null) {
			throw new BusinessException("Calculation Request not found.");
		} else if(!dfcr.getStatus().equals("calculationCompleted"))
		{
			throw new BusinessException("Calculation is in progress now. Try later.");
		}

		exchange.getOut().setBody(dfcr.getDistanceFactor());
	}

	public void createDistanceFactorCalculationRequest(Exchange exchange) {
		String username = getFieldFromExchangeHeader(exchange, "username");
		String targetUser = getFieldFromExchangeHeader(exchange, "targetUser");

		DistanceFactorCalculationRequest dfcr = new DistanceFactorCalculationRequest();
		dfcr.setSourceUser(username);
		dfcr.setTargetUser(targetUser);
		dfcr.setStatus("pending");

		String requestID = UUID.randomUUID().toString();
		distanceFactorCalculationRequestsCache.put(requestID, dfcr);

		exchange.getOut().setBody(requestID);
	}

	public void postMessage(Exchange exchange) {
		String username = getFieldFromExchangeHeader(exchange, "username");

		validateUser(username);

		Message msg = new Message();
		msg.setDate(new Date());
		msg.setMessage(getFieldFromExchangeBody(exchange, "message"));

		mongoUserDao.postMessage(username, msg);
	}

	public List<UserResource> getUsers(Exchange exchange) {
		@SuppressWarnings("unchecked")
		List<String> usernameList = ((List<String>) exchange.getIn().getBody());
		List<User> userList = mongoUserDao.getUsers(usernameList);
		List<UserResource> userResourceList = new ArrayList<UserResource>();
		for (User user : userList) {
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

	public void endUserCreation(Exchange exchange) {
		String username = exchange.getIn().getBody(String.class);
		try
		{
			String id = neo4jUserDao.saveUser(username);
			if(id == null)
			{
				throw new NeededRetryException("User " + username + "not yet saved to Neo4j. Returned ID is null");
			}
			mongoUserDao.setUserStatusCreated(username);
		} catch (Exception e)
		{
			LOGGER.error(e.getMessage() + " Retrying...", e);
			throw new NeededRetryException(e.getMessage() + " Retrying...", e);
		}
	}
}
