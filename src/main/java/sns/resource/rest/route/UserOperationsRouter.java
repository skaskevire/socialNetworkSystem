package sns.resource.rest.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import sns.resource.rest.entity.UserResource;
@Component
public class UserOperationsRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:deleteUser")
                .to("direct:userService.markAsDeleted")
                .to("actsivemq:queue:user-mongo-delete-queue?exchangePattern=InOnly")
                .to("direct:emptyResponse");
        from("direct:addUser")
                .to("direct:userService.save")
                .to("actsivemq:queue:user-queue?exchangePattern=InOnly")
                .to("direct:emptyResponse");
        from("direct:findUser")
                .to("direct:userService.filterUsers")
                .to("mock:endFind");
    }
}
