package sns.resource.rest.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MessagingOperationsRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:update").to("direct:userService.postMessage")
                .to("direct:emptyResponse");
        from("direct:retrieveSpecifyingUserData")
                .to("direct:userService.getAllFriendMessages");
        from("direct:retrieveSpecifyingNetworkUserData")
                .to("direct:userService.getAllNetworkMessages");
    }
}
