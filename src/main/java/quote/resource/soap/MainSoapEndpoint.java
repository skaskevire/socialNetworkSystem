package quote.resource.soap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.epam.quote.GetQuoteRequest;

import quote.resource.QuoteRequestBusinessModel;

@Component
public class MainSoapEndpoint extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		from("cxf:/quote?serviceClass=com.epam.quote.Quote").
		process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				GetQuoteRequest gqr =  exchange.getIn().getBody(GetQuoteRequest.class);
				QuoteRequestBusinessModel gqrbm = new QuoteRequestBusinessModel();
				gqrbm.setName(gqr.getMessage().getName());
				gqrbm.setProcessingType("SOAP");
				exchange.getOut().setBody(gqrbm);
			}
		}).to("direct:getQuote");
	}
}
