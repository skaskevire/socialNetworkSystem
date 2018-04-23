package quote.resource.rest;

import java.util.LinkedHashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import quote.resource.entity.User;

@Component
public class MainRestEndpoint extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		restConfiguration()
		.component("servlet")
		.contextPath("/")
		.bindingMode(RestBindingMode.json);
	
	rest("/users/add")
		.post("/").to("direct:convert");
	from("direct:convert").process(new Processor() {

		@Override
		public void process(Exchange exchange) throws Exception {

			//User user =  (User) exchange.getIn().getBody(User.class);
		//	QuoteRequestBusinessModel gqr = new QuoteRequestBusinessModel();
		//	gqr.setName(name);
		//	gqr.setProcessingType("REST");
		//	exchange.getOut().setBody(gqr);
			User user = new User();
			user.setCity((String) ((LinkedHashMap<String, ?>)exchange.getIn().getBody()).get("city"));
			user.setBdate((String) ((LinkedHashMap<String, ?>)exchange.getIn().getBody()).get("bdate"));
			user.setName((String) ((LinkedHashMap<String, ?>)exchange.getIn().getBody()).get("name"));

			exchange.getOut().setBody(user);
		}
	}).to("bean:neo4jUserDao?method=saveUser");

	}
}
