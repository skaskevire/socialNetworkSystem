package sns.resource.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import sns.dao.entity.Message;
import sns.dao.entity.User;

@Component
public class UserMessageAggregator{
	public void aggregate(Exchange exchange)
	{
		List<User> users = ((List<User>)exchange.getIn().getBody());
		List<Message> messages = new ArrayList<Message>();
		for(User user: users)
		{
			if(user.getMessages() != null)
			{
				messages.addAll(user.getMessages());
			}
		}
		exchange.getOut().setBody(messages);
	}
}
