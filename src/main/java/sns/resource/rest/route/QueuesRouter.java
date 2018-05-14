package sns.resource.rest.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QueuesRouter extends RouteBuilder {

    @Value("${sns.activemq.redeliveryDelay}")
    private Integer redeliveryDelay;

    @Value("${sns.activemq.maximumRedeliveries}")
    private Integer maximumRedeliveries;

    @Override
    public void configure() throws Exception {
        from("actsivemq:queue:user-queue")
                .errorHandler(
                        defaultErrorHandler()
                                .maximumRedeliveries(maximumRedeliveries)
                                .redeliveryDelay(redeliveryDelay))
                .to("direct:userService.endUserCreation");
        from("actsivemq:queue:user-mongo-delete-queue")
                .errorHandler(
                        defaultErrorHandler()
                                .maximumRedeliveries(maximumRedeliveries)
                                .redeliveryDelay(redeliveryDelay))
                .to("direct:userService.deleteFromMongo")
                .to("actsivemq:queue:user-neo4j-delete-queue?exchangePattern=InOnly");
        from("actsivemq:queue:user-neo4j-delete-queue")
                .errorHandler(
                        defaultErrorHandler()
                                .maximumRedeliveries(maximumRedeliveries)
                                .redeliveryDelay(redeliveryDelay))
                .to("direct:userService.deleteFromNeo4j");
        from("actsivemq:queue:user-distance-factor-calculation-queue")
                .to("direct:userService.distanceFactor");
    }
}
