package quote.resource.soap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.epam.quote.GetQuoteResponse;
import com.epam.quote.GetQuoteResponseMessage;

import quote.resource.QuoteResponseBusinessModel;

@Component
public class QuoteSoapResponseConverter implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		QuoteResponseBusinessModel qbm = exchange.getIn().getBody(QuoteResponseBusinessModel.class);
		
		GetQuoteResponse gqr = new GetQuoteResponse();
		GetQuoteResponseMessage gqrm = new GetQuoteResponseMessage();
		gqr.setMessage(gqrm);
		gqrm.setMessage(qbm.getMessage());
		gqrm.setTime(qbm.getTime());

		exchange.getOut().setBody(gqr);
	}

}
