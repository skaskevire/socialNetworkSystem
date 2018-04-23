package quote.core.aggregator;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import quote.resource.QuoteResponseBusinessModel;

public class QuoteAggregationStrategy implements AggregationStrategy {
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (oldExchange == null ) {
			return newExchange;
		}

		QuoteResponseBusinessModel quoteToMerge = (QuoteResponseBusinessModel) newExchange.getIn().getBody();
		
		QuoteResponseBusinessModel result = null;
		if(oldExchange.getIn().getBody() != null && oldExchange.getIn().getBody() instanceof QuoteResponseBusinessModel)
		{
			result = (QuoteResponseBusinessModel) oldExchange.getIn().getBody();
		}
		
		if (result != null) {
			if (result.getMessage() != null) {
				quoteToMerge.setMessage(result.getMessage());
			}
			if (result.getTime() != null) {
				quoteToMerge.setTime(result.getTime());
			}
			quoteToMerge.setProcessingType(result.getProcessingType());
		} else {
			oldExchange.getOut().setBody(quoteToMerge);
		}

		return oldExchange;
	}

}