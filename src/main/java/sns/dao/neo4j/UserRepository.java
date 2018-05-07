package sns.dao.neo4j;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import sns.dao.entity.User;

public interface UserRepository extends Neo4jRepository<User, Long> {
	@Query("MATCH (a:user),(b:user) WHERE a.name = {sourceuser} AND b.name = {targetuser} AND NOT a.name = b.name AND NOT (a)-[:FRIEND]->(b) MERGE (a)-[r:FRIENDSHIP_REQUESTED]->(b) RETURN 0")
	public Integer addUserToFriend(@Param("sourceuser") String sourceuser, @Param("targetuser") String targetuser);

	@Query("MATCH (a:user),(b:user) WHERE a.name = {user} AND (a)<-[:FRIENDSHIP_REQUESTED]-(b) RETURN b.name")
	List<String> getIncomingFriendshipRequestedRelations(@Param("user") String user);

	@Query("MATCH (a:user)-[r:FRIENDSHIP_REQUESTED]->(b:user)" + " WHERE a.name = {requestor} AND b.name = {acceptor} "
			+ "CREATE (a)-[r2:FRIEND]->(b) CREATE (a)<-[r3:FRIEND]-(b)" + "  SET r2 = r WITH r delete r RETURN 0")
	public Integer acceptFrienship(@Param("acceptor") String acceptor, @Param("requestor") String requestor);

	@Query("MERGE (n:user { name: {username}}) RETURN ID(n)")
	public String createOrUpdate(@Param("username") String username);

	@Query("MATCH (u:user { name: {username} })-[:FRIEND]->(x) RETURN x.name")
	public List<String> getNearestNodes(@Param("username") String username);

	@Query("MATCH (u:user { name: {username} })-[:FRIEND*1..50]->(x) RETURN DISTINCT x.name")
	public List<String> exploreNetwork(@Param("username") String username);

	@Query("MATCH (u:user { name: {username}})-[r:FRIEND]-(v:user{name : {friendToRemove}}) DELETE r RETURN 0")
	public List<Integer> removeFriendRelation(@Param("username") String username,
			@Param("friendToRemove") String friendToRemove);

	@Query("MATCH (u:user { name: {username} }),(v:user { name: {targetuser} }), p = shortestPath((u)-[:FRIEND*]-(v))\r\n"
			+ "RETURN length(p)")
	public int distanceFactor(@Param("username") String username, @Param("targetuser") String targetuser);
	@Query("MATCH (n:user) RETURN COUNT(n)")
	public long userCount();
	
	@Query("MATCH (u:user) WHERE u.name = {username} RETURN u._id")
	public String getUserID(@Param("username")String username);
	
	@Query("MATCH (u:user) WHERE u.name = {username} DELETE u RETURN 0")
	public Integer deleteUser(@Param("username") String username);
}
