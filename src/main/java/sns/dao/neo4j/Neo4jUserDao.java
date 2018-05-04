package sns.dao.neo4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sns.dao.entity.User;

@Component
public class Neo4jUserDao {
	@Autowired
	UserRepository repository;

	public void saveUser(User user) {
		repository.createOrUpdate(user.getName());
	}

	public Integer addFriendshipRequestedRelation(String sourceUser, String targetUser) {
		return repository.addUserToFriend(sourceUser, targetUser);
	}

	public List<String> getIncomingFriendshipRequestedRelations(String user)
	{	
		return repository.getIncomingFriendshipRequestedRelations(user);
	}

	public Integer acceptInvitation(String acceptor, String requestor)
	{
		return repository.acceptFrienship(acceptor, requestor);
	}
	
	public List<String> getNearestNodes(String currentNode)
	{
		return repository.getNearestNodes(currentNode);		
	}
	
	public List<String> getAllConnectedNodes(String username)
	{
		return repository.exploreNetwork(username);		
	}
	
	public List<Integer> removeFriendRelation(String sourceNode, String targetNode)
	{
		return repository.removeFriendRelation(sourceNode, targetNode);
	}
	
	public Integer distanceFactor(String sourceNode, String targetNode)
	{
		return repository.distanceFactor(sourceNode, targetNode);
	}
	
	public Long userCount()
	{
		return repository.userCount();
	}
	
	public Boolean userExists(String username)
	{
		Boolean result = false;
		if(repository.getUserID(username)!=null)
		{
			result = true;
		}
		
		return result;
	}
}
