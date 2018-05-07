package sns.resource.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import sns.exception.NotYetCreatedException;
import sns.resource.rest.entity.UserResource;


@Component
public class UserRouter extends RouteBuilder{
	@Override
	public void configure() throws Exception {
		restConfiguration()
		.component("servlet")
		.contextPath("/")
		.bindingMode(RestBindingMode.json);
	onException(NotYetCreatedException.class)
		.handled(false);
	onException(Exception.class)
		.handled(true)
		.to("bean:exceptionProcessor");
	
	rest("/users")
		.post("/add").type(UserResource.class)
			.to("direct:addUser")
		.get("/find").to("direct:findUser")
		.get("/count").to("bean:userService?method=userCount")
		.delete("/delete/{username}")
			.to("bean:userService?method=delete");
	rest("/users/{username}/friends")
		.post("/add/{targetUser}")//
			.to("direct:addToFriends")
		.delete("/remove/{friendToRemove}")
			.to("direct:removeFriend")
		.get("/distanceFactor/{targetUser}")
			.to("bean:userService?method=distanceFactor");
	rest("/users/{username}/friends/invitations")
		.get("/get").to("bean:userService?method=getInvitations")
		.post("/accept/{requestor}").to("direct:acceptInvitation");
	rest("/users/{username}/friends/explore")
		.get("/users").to("direct:returnFriendUsers")
		.get("/network").to("direct:returnNetworkUsers");
	rest("/users/{username}/messages")
		.post("/post").to("direct:update")
		.get("/friends").to("direct:retrieveSpecifyingUserData")
		.get("/network").to("direct:retrieveSpecifyingNetworkUserData");

	from("direct:returnNetworkUsers")
		.to("bean:userService?method=exploreNetwork")
		.to("bean:userService?method=getUsers");
	from("direct:returnFriendUsers")
		.to("bean:userService?method=exploreUsers")
		.to("bean:userService?method=getUsers");
	from("direct:addToFriends")
		.to("bean:userService?method=addToFriends")
		.to("direct:emptyResponse");	
	from("direct:removeFriend")
		.to("bean:userService?method=removeFriend")
		.to("direct:emptyResponse");
	from("direct:addUser")
		.to("bean:userService?method=save")
		.to("actsivemq:queue:user-queue?exchangePattern=InOnly")
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
		.to("bean:userService?method=getAllNetworkMessages");
	from("direct:emptyResponse").setBody().constant("");	
	from("actsivemq:queue:user-queue")
		.errorHandler(			
			defaultErrorHandler()
				.maximumRedeliveries(-1)
				.redeliveryDelay(1000l))
		.to("bean:userService?method=endUserCreation");
	}
}
