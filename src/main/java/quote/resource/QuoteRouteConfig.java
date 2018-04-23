package quote.resource;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import quote.core.aggregator.QuoteAggregationStrategy;

@Component
public class QuoteRouteConfig extends RouteBuilder {
	@Override
	public void configure() {
		from("direct:getQuote")
			.enrich("bean:nameVendorBean", new QuoteAggregationStrategy())
			.enrich("bean:dateVendorBean", new QuoteAggregationStrategy())
			.choice()
			.when(simple("${body.processingType} == 'REST'"))
				.to("direct:restResponseRoute")
			.otherwise()
				.to("direct:soapResponseRoute");		
		from("direct:soapResponseRoute").to("bean:quoteSoapResponseConverter").to("mock:endS");
		from("direct:restResponseRoute").to("bean:quoteRestResponseConverter").to("mock:endR");		
	}
}