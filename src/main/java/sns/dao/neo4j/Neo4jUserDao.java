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

	public void addFriendshipRequestedRelation(String sourceUser, String targetUser) {
		repository.addUserToFriend(sourceUser, targetUser);
	}

	public List<String> getIncomingFriendshipRequestedRelations(String user)
	{	
		return repository.getIncomingFriendshipRequestedRelations(user);
	}

	public void acceptInvitation(String acceptor, String requestor)
	{
		repository.acceptFrienship(acceptor, requestor);
	}
	
	public List<String> getNearestNodes(String currentNode)
	{
		return repository.getNearestNodes(currentNode);		
	}
	
	public List<String> getAllConnectedNodes(String username)
	{
		return repository.exploreNetwork(username);		
	}
	
	public void removeFriendRelation(String sourceNode, String targetNode)
	{
		repository.removeFriendRelation(sourceNode, targetNode);
	}
	
	public int distanceFactor(String sourceNode, String targetNode)
	{
		return repository.distanceFactor(sourceNode, targetNode);
	}
}
