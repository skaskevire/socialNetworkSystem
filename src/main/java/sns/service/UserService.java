package sns.service;

import java.net.URL;
import java.text.ParseException;
import java.util.*;

import org.apache.camel.Body;
import org.apache.camel.Consume;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
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

    @Consume(uri = "direct:userService.save")
    public String save(@Body UserResource userResource) {
        User user = userResourceConverter.convert(userResource);
        user.setStatus(UserStatusEnum.pendingCreation.name());
        mongoUserDao.saveUser(user);
        return user.getName();
    }

    @Consume(uri = "direct:userService.markAsDeleted")
    public String markAsDeleted(@Header("username") String user) {
        validateUser(user);
        mongoUserDao.setUserStatusPendingRemoval(user);
        return user;
    }

    @Consume(uri = "direct:userService.deleteFromMongo")
    public String deleteFromMongo(@Body String user) {
        try {
            mongoUserDao.removeUser(user);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + " Retrying...", e);
            throw new NeededRetryException(e.getMessage() + " Retrying...", e);
        }
        return user;
    }

    @Consume(uri = "direct:userService.deleteFromNeo4j")
    public String deleteFromNeo4j(@Body String user) {
        try {
            Integer response = neo4jUserDao.deleteUser(user);
            if (response == null) {
                throw new NeededRetryException(
                        "User " + user + "not yet removed from Neo4j. Invalid response from DB");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + " Retrying...", e);
            throw new NeededRetryException(e.getMessage() + " Retrying...", e);
        }
        return user;
    }

    @Consume(uri = "direct:userService.addToFriends")
    public void addToFriends(@Header("username") String username, @Header("targetUser") String targetUserName) {
        validateUser(username);
        validateUser(targetUserName);

        Integer responseCode = neo4jUserDao.addFriendshipRequestedRelation(username, targetUserName);
        if (responseCode == null) {
            throw new BusinessException("AddFriend operation failed");
        }
    }

    @Consume(uri = "direct:userService.getInvitations")
    public List<String> getInvitations(@Header("username") String username) {
        validateUser(username);
        return neo4jUserDao.getIncomingFriendshipRequestedRelations(username);
    }

    @Consume(uri = "direct:userService.acceptInvitation")
    public void acceptInvitation(@Header("username") String username, @Header("requestor") String requestorName) {
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

    @Consume(uri = "direct:userService.exploreUsers")
    public List<String> exploreUsers(@Header("username") String username) {
        validateUser(username);
        return neo4jUserDao.getNearestNodes(username);
    }

    @Consume(uri = "direct:userService.getAllFriendMessages")
    public List<MessageResource> getAllFriendMessages(@Header("username") String username) {
        List<MessageResource> mrList = new ArrayList<MessageResource>();
        List<Message> mList = mongoUserDao.getUserMessages(exploreUsers(username));
        if (mList != null) {
            for (Message message : mList) {
                mrList.add(messageConverter.convert(message));
            }
        }

        return mrList;
    }

    @Consume(uri = "direct:userService.getAllNetworkMessages")
    public List<MessageResource> getAllNetworkMessages(@Header("username") String username) {
        List<MessageResource> mrList = new ArrayList<MessageResource>();
        List<Message> mList = mongoUserDao.getUserMessages(exploreNetwork(username));
        if (mList != null) {
            for (Message message : mList) {
                mrList.add(messageConverter.convert(message));
            }
        }

        return mrList;
    }

    @Consume(uri = "direct:userService.exploreNetwork")
    public List<String> exploreNetwork(@Header("username") String username) {
        validateUser(username);
        return neo4jUserDao.getAllConnectedNodes(username);
    }

    @Consume(uri = "direct:userService.removeFriend")
    public void removeFriend(@Header("username") String username, @Header("friendToRemove") String friendToRemove) {
        validateUser(username);
        validateUser(friendToRemove);

        List<Integer> responseCodes = neo4jUserDao.removeFriendRelation(username, friendToRemove);
        if (responseCodes == null || responseCodes.size() != 2) {
            throw new BusinessException("RemoveUser operation failed");
        }
    }

    @Consume(uri = "direct:userService.distanceFactor")
    public void distanceFactor(@Body String requestID) {
        DistanceFactorCalculationRequest dfcr = distanceFactorCalculationRequestsCache.get(requestID);
        Integer distanceFactor = neo4jUserDao.distanceFactor(dfcr.getSourceUser(), dfcr.getTargetUser());
        if (distanceFactor == null) {
            throw new BusinessException("DistanceFactor operation failed: sourceUser or targetUser name incorrect");
        }
        dfcr.setStatus("calculationCompleted");
        dfcr.setDistanceFactor(distanceFactor);
    }

    @Consume(uri = "direct:userService.returnDistanceFactor")
    public Integer returnDistanceFactor(@Header("requestID") String requestID) {
        DistanceFactorCalculationRequest dfcr = distanceFactorCalculationRequestsCache.get(requestID);
        if (dfcr == null) {
            throw new BusinessException("Calculation Request not found.");
        } else if (!dfcr.getStatus().equals("calculationCompleted")) {
            throw new BusinessException("Calculation is in progress now. Try later.");
        }
        return dfcr.getDistanceFactor();
    }

    @Consume(uri = "direct:userService.createDistanceFactorCalculationRequest")
    public String createDistanceFactorCalculationRequest(@Header("username") String username, @Header("targetUser") String targetUser) {
        DistanceFactorCalculationRequest dfcr = new DistanceFactorCalculationRequest();
        dfcr.setSourceUser(username);
        dfcr.setTargetUser(targetUser);
        dfcr.setStatus("pending");
        String requestID = UUID.randomUUID().toString();
        distanceFactorCalculationRequestsCache.put(requestID, dfcr);
        return requestID;
    }

    @Consume(uri = "direct:userService.postMessage")
    public void postMessage(@Header("username") String username, @Body LinkedHashMap<String, ?> messages) {
        validateUser(username);
        Message msg = new Message();
        msg.setDate(new Date());
        msg.setMessage((String) messages.get("message"));
        mongoUserDao.postMessage(username, msg);
    }

    @Consume(uri = "direct:userService.getUsers")
    public List<UserResource> getUsers(@Body List<String> usernameList) {
        @SuppressWarnings("unchecked")
        List<User> userList = mongoUserDao.getUsers(usernameList);
        List<UserResource> userResourceList = new ArrayList<UserResource>();
        for (User user : userList) {
            userResourceList.add(userConverter.convert(user));
        }
        return userResourceList;
    }

    @Consume(uri = "direct:userService.filterUsers")
    public List<User> filterUsers(
            @Header("name") String name,
            @Header("city") String city,
            @Header("bdateRangeFloor") String bdateRangeFloor,
            @Header("bdateRangeCeiling") String bdateRangeCeiling) throws ParseException {
        return mongoUserDao.find(name, bdateRangeFloor, bdateRangeCeiling, city);
    }

    @Consume(uri = "direct:userService.userCount")
    public Long userCount() {
        return neo4jUserDao.userCount();
    }

    @Consume(uri = "direct:userService.endUserCreation")
    public void endUserCreation(@Body String username) {
        try {
            String id = neo4jUserDao.saveUser(username);
            if (id == null) {
                throw new NeededRetryException("User " + username + "not yet saved to Neo4j. Returned ID is null");
            }
            mongoUserDao.setUserStatusCreated(username);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + " Retrying...", e);
            throw new NeededRetryException(e.getMessage() + " Retrying...", e);
        }
    }
}
