package sns.resource.rest.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import sns.exception.NeededRetryException;
import sns.resource.rest.entity.UserResource;


@Component
public class RestEndpointRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .contextPath("/")
                .bindingMode(RestBindingMode.json);
        onException(NeededRetryException.class)
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
                .to("direct:deleteUser");
        rest("/users/{username}/friends")
                .post("/add/{targetUser}")
                .to("direct:addToFriends")
                .delete("/remove/{friendToRemove}")
                .to("direct:removeFriend")
                .post("/requestDistanceFactorCalculatuion/{targetUser}")
                .to("direct:createDistanceFactorCalculationRequest")
                .get("/distanceFactorCalculationResult/{requestID}")
                .to("bean:userService?method=returnDistanceFactor");

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

        from("direct:emptyResponse").setBody().constant("");
    }
}
