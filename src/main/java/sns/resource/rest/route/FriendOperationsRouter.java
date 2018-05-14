package sns.resource.rest.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FriendOperationsRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:addToFriends")
                .to("direct:userService.addToFriends")
                .to("direct:emptyResponse");
        from("direct:removeFriend")
                .to("direct:userService.removeFriend")
                .to("direct:emptyResponse");
        from("direct:returnNetworkUsers")
                .to("direct:userService.exploreNetwork")
                .to("direct:userService.getUsers");
        from("direct:returnFriendUsers")
                .to("direct:userService.exploreUsers")
                .to("direct:userService.getUsers");
        from("direct:acceptInvitation")
                .to("direct:userService.acceptInvitation")
                .to("direct:emptyResponse");
        from("direct:createDistanceFactorCalculationRequest")
                .to("direct:userService.createDistanceFactorCalculationRequest")
                .wireTap("actsivemq:queue:user-distance-factor-calculation-queue");
    }
}
