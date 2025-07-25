package microservice.users.infrastructure.persistence.mappers;

import microservice.users.core.models.User;
import microservice.users.infrastructure.persistence.models.UserModel;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper {
    User toDomain(UserModel userModel);
    UserModel toModel(User user);
}
