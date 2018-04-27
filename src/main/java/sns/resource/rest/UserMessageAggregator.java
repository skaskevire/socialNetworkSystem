package sns.resource.rest;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import sns.dao.entity.Message;
import sns.dao.entity.User;

@Component
public class UserMessageAggregator{
	public void aggregate(Exchange exchange)
	{
		@SuppressWarnings("unchecked")
		List<User> users = ((List<User>)exchange.getIn().getBody());
		Set<Message> messages = new TreeSet<Message>();
		for(User user: users)
		{
			if(user.getMessages() != null)
			{
				for(Message msg : user.getMessages())
				{
					msg.setUser(user.getName());
					messages.add(msg);
				}
				
			}
		}
		exchange.getOut().setBody(messages);
	}
}
