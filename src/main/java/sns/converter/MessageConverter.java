package sns.converter;

import org.springframework.stereotype.Component;

import sns.dao.entity.Message;
import sns.resource.rest.entity.MessageResource;

@Component
public class MessageConverter implements Converter<Message, MessageResource>{

	@Override
	public MessageResource convert(Message input) {
		MessageResource result = null;
		if(input!=null)
		{
			result = new MessageResource();
			result.setDate(input.getDate());
			result.setMessage(input.getMessage());
			result.setUser(input.getUser());
		}

		return result;
	}

}
