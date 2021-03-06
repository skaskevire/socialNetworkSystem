package sns.dao.mongo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import sns.dao.entity.Message;
import sns.dao.entity.User;
import sns.dao.entity.UserStatusEnum;

@Component
public class MongoUserDao {
	@Autowired
	MongoOperations mongoOperations;
	
	public void saveUser(User user)
	{
		Query query = new Query();
		query.addCriteria(Criteria.where("name").is(user.getName()));
		
		Update update = new Update();
		update.set("status", user.getStatus());
		update.set("bdate", user.getBdate());
		update.set("city", user.getCity());
		update.set("name", user.getName());
		mongoOperations.upsert(query, update, User.class);
	}
	
	public void removeUser(String username)
	{
		Query query = new Query();
		query.addCriteria(Criteria.where("name").is(username));
		mongoOperations.remove(query, User.class);
	}

	public void postMessage(String username, Message message)
	{
		message.setUser(username);
		mongoOperations.save(message);
	}
	
	public List<Message> getUserMessages(List<String> usernames)
	{
		Query query = new Query();
		List<Message> messages = null;
		List<Criteria> criterias = new ArrayList<>();
		
		for(String username: usernames)
		{
			criterias.add(Criteria.where("user").is(username));
		}
		if(criterias.size() > 0)
		{
			query.addCriteria(new Criteria().orOperator((Criteria[]) criterias.toArray(new Criteria[criterias.size()]))).with(new Sort(Sort.Direction.ASC, "date"));
			messages = mongoOperations.find(query, Message.class);
		}
		
		return messages;
	}
	
	
	public User getUser(String username)
	{
		Query query = new Query();
		query.addCriteria(Criteria.where("name").is(username));
		return mongoOperations.findOne(query, User.class);
	}
	
	public List<User> getUsers(List<String> usernameList)
	{
		Query query = new Query();
		List<User> users = new ArrayList<>();
		List<Criteria> criterias = new ArrayList<>();
		
		for(String username: usernameList)
		{
			criterias.add(Criteria.where("name").is(username));
		}
		if(criterias.size() > 0)
		{
			query.addCriteria(new Criteria().orOperator((Criteria[]) criterias.toArray(new Criteria[criterias.size()]))).with(new Sort(Sort.Direction.ASC, "name"));
			users = mongoOperations.find(query, User.class);
		}

		return users;
	}
	
	public List<User> find(String usernamePattern, String bdateRangeFloor, String bdateRangeCeiling, String city) throws ParseException
	{
		List<User> users = null;
		SimpleDateFormat dateformat = new SimpleDateFormat("MM.dd.yyyy");
		List<Criteria> criterias = new ArrayList<>();
		if(usernamePattern != null && !usernamePattern.isEmpty())
		{
			criterias.add(Criteria.where("name").regex(usernamePattern));
		}
		if(city != null && !city.isEmpty())
		{
			criterias.add(Criteria.where("city").is(city));
		}
		if(bdateRangeFloor!=null && !bdateRangeFloor.isEmpty())
		{
			criterias.add(Criteria.where("bdate").gt(dateformat.parse(bdateRangeFloor)));
		}
		if(bdateRangeCeiling!=null && !bdateRangeCeiling.isEmpty())
		{
			criterias.add(Criteria.where("bdate").lt(dateformat.parse(bdateRangeCeiling)));
		}
		Query query = new Query();
		if(criterias.size() > 0)
		{
			query.addCriteria(new Criteria().andOperator((Criteria[]) criterias.toArray(new Criteria[criterias.size()])));
			users = mongoOperations.find(query, User.class);
		}

		return users;
	}
	
	public Long count()
	{
		  Query query = new Query();   
		  return mongoOperations.count(query, User.class);
	}
	
	public void setUserStatusCreated(String username) {
		Update update = new Update();
		update.set("status", UserStatusEnum.created.name());
		Query q = new Query();
		
		mongoOperations.updateFirst(q.addCriteria(Criteria.where("name").is(username)), update, User.class);
	}
	
	public void setUserStatusPendingRemoval(String username) {
		Update update = new Update();
		update.set("status", UserStatusEnum.pendingRemoval.name());
		Query q = new Query();
		
		mongoOperations.updateFirst(q.addCriteria(Criteria.where("name").is(username)), update, User.class);
	}
}
