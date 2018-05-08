package sns.resource.rest.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FriendOperationsRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:addToFriends")
                .to("bean:userService?method=addToFriends")
                .to("direct:emptyResponse");
        from("direct:removeFriend")
                .to("bean:userService?method=removeFriend")
                .to("direct:emptyResponse");
        from("direct:returnNetworkUsers")
                .to("bean:userService?method=exploreNetwork")
                .to("bean:userService?method=getUsers");
        from("direct:returnFriendUsers")
                .to("bean:userService?method=exploreUsers")
                .to("bean:userService?method=getUsers");
        from("direct:acceptInvitation")
                .to("bean:userService?method=acceptInvitation")
                .to("direct:emptyResponse");
        from("direct:createDistanceFactorCalculationRequest")
                .to("bean:userService?method=createDistanceFactorCalculationRequest")
                .to("actsivemq:queue:user-distance-factor-calculation-queue?exchangePattern=InOnly");
    }
}
