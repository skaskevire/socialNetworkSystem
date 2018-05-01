package sns.converter;

import org.springframework.stereotype.Component;

import sns.dao.entity.User;
import sns.resource.rest.entity.UserResource;

@Component
public class UserConverter implements Converter<User, UserResource>{

	@Override
	public UserResource convert(User input) {
		UserResource result = null;
		if(input != null)
		{
			result = new UserResource();
			result.setBdate(input.getBdate());
			result.setCity(input.getCity());
			result.setName(input.getName());
		}

		return result;
	}

}
