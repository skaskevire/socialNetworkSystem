package sns.resource.rest.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class QueuesRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("actsivemq:queue:user-queue")
                .errorHandler(
                        defaultErrorHandler()
                                .maximumRedeliveries(-1)
                                .redeliveryDelay(1000l))
                .to("bean:userService?method=endUserCreation");
        from("actsivemq:queue:user-mongo-delete-queue")
                .errorHandler(
                        defaultErrorHandler()
                                .maximumRedeliveries(-1)
                                .redeliveryDelay(1000l))
                .to("bean:userService?method=deleteFromMongo")
                .to("actsivemq:queue:user-neo4j-delete-queue?exchangePattern=InOnly");
        from("actsivemq:queue:user-neo4j-delete-queue")
                .errorHandler(
                        defaultErrorHandler()
                                .maximumRedeliveries(-1)
                                .redeliveryDelay(1000l))
                .to("bean:userService?method=deleteFromNeo4j");
        from("actsivemq:queue:user-distance-factor-calculation-queue")
                .to("bean:userService?method=distanceFactor");
    }
}
