package quote.core.vendors;

import java.util.Date;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import quote.resource.QuoteResponseBusinessModel;

@Component(value = "dateVendorBean")
public class DateVendor {

	public void append(Exchange exchange) {
		QuoteResponseBusinessModel qrbm = exchange.getIn().getBody(QuoteResponseBusinessModel.class);
		if (qrbm != null) {
			qrbm.setTime(new Date().toString());
			exchange.getOut().setBody(qrbm);
		} else {
			exchange.getOut().setBody(new QuoteResponseBusinessModel(null, new Date().toString(), null));
		}
	}
}