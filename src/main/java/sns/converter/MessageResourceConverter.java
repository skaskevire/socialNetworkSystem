package sns.converter;

import org.springframework.stereotype.Component;

import sns.dao.entity.Message;
import sns.resource.rest.entity.MessageResource;

@Component
public class MessageResourceConverter implements Converter<MessageResource, Message>{

	@Override
	public Message convert(MessageResource input) {
		Message result = null;
		if(input!=null)
		{
			result = new Message();
			result.setDate(input.getDate());
			result.setMessage(input.getMessage());
			result.setUser(input.getUser());
		}

		return result;
	}

}
