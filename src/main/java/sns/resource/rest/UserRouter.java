package sns.resource.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import sns.dao.entity.User;


@Component
public class UserRouter extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		restConfiguration()
		.component("servlet")
		.contextPath("/")
		.bindingMode(RestBindingMode.json);
	
	rest("/users")
		.post("/add").type(User.class).outType(String.class)
			.to("direct:addUser")
		.get("/find").to("direct:findUser")
		.get("/generate/{numberOfUsers}").to("bean:userService?method=generateUsersAndRelations");
	rest("/users/{username}/friends")
		.post("/add/{targetUser}")
			.to("direct:addToFriends")
		.delete("/remove/{friendToRemove}")
			.to("direct:removeFriend")
		.get("/distanceFactor/{targetUser}")
			.to("bean:userService?method=distanceFactor");
	rest("/users/{username}/friends/invitations")
		.get("/get").to("bean:userService?method=getInvitations")
		.post("/accept/{requestor}").to("direct:acceptInvitation");
	rest("/users/{username}/friends/explore")
		.get("/users").to("bean:userService?method=exploreUsers")
		.get("/network").to("bean:userService?method=exploreNetwork");	
	rest("/users/{username}/messages")
		.post("/post").to("direct:update")
		.get("/friends").to("direct:retrieveSpecifyingUserData")
		.get("/network").to("direct:retrieveSpecifyingNetworkUserData");
	
	from("direct:addToFriends")
		.to("bean:userService?method=addToFriends")
		.to("direct:emptyResponse");	
	from("direct:removeFriend")
		.to("bean:userService?method=removeFriend")
		.to("direct:emptyResponse");
	from("direct:addUser")
		.to("bean:userService?method=save")
		.to("direct:emptyResponse");		
	from("direct:findUser")
		.to("bean:userService?method=filterUsers")
		.to("mock:endFind");
	from("direct:acceptInvitation")
		.to("bean:userService?method=acceptInvitation")
		.to("direct:emptyResponse");
	from("direct:update").to("bean:userService?method=postMessage")
		.to("direct:emptyResponse");	
	from("direct:retrieveSpecifyingUserData")
		.to("bean:userService?method=getAllFriendMessages");

	from("direct:retrieveSpecifyingNetworkUserData")
		.to("bean:userService?method=exploreNetwork")
		.to("bean:userService?method=getUsers")
		.to("bean:userMessageAggregator");	
	from("direct:emptyResponse").setBody().constant("");	
	}
}
