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
import org.springframework.stereotype.Component;

import sns.dao.entity.Message;
import sns.dao.entity.User;

@Component
public class MongoUserDao {
	@Autowired
	MongoOperations mongoOperations;
	
	public void saveUser(User user)
	{
		mongoOperations.save(user);
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
		List<User> users = new ArrayList<>();
		for(String username : usernameList)
		{
			Query query = new Query();
			query.addCriteria(Criteria.where("name").is(username));
			users.add(mongoOperations.findOne(query, User.class));
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
}
