package sns.resource.rest.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MessagingOperationsRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:update").to("bean:userService?method=postMessage")
                .to("direct:emptyResponse");
        from("direct:retrieveSpecifyingUserData")
                .to("bean:userService?method=getAllFriendMessages");
        from("direct:retrieveSpecifyingNetworkUserData")
                .to("bean:userService?method=getAllNetworkMessages");
    }
}
