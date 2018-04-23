package quote.core.vendors;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import quote.resource.QuoteRequestBusinessModel;
import quote.resource.QuoteResponseBusinessModel;

@Component(value = "nameVendorBean")
public class NameVendor {
	private static final String MSG_PATTERN = "Hello %s";

	public void append(Exchange exchange) {
		QuoteRequestBusinessModel qrbm = exchange.getIn().getBody(QuoteRequestBusinessModel.class);

		String name = qrbm.getName();
		exchange.getOut().setBody(
				new QuoteResponseBusinessModel(qrbm.getProcessingType(), null, String.format(MSG_PATTERN, name)));
	}
}