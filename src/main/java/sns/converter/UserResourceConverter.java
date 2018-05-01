package sns.converter;

import org.springframework.stereotype.Component;

import sns.dao.entity.User;
import sns.resource.rest.entity.UserResource;

@Component
public class UserResourceConverter implements Converter<UserResource, User>{

	@Override
	public User convert(UserResource input) {
		User result = null;
		if(input != null)
		{
			result = new User();
			result.setBdate(input.getBdate());
			result.setCity(input.getCity());
			result.setName(input.getName());
		}

		return result;
	}

}
