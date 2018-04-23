package quote.resource.rest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import quote.resource.QuoteResponseBusinessModel;
import quote.resource.entity.Quote;

@Component
public class QuoteRestResponseConverter implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		QuoteResponseBusinessModel qbm = exchange.getIn().getBody(QuoteResponseBusinessModel.class);
		
		Quote quote = new Quote(qbm.getTime(), qbm.getMessage());
		exchange.getOut().setBody(quote);
	}

}
