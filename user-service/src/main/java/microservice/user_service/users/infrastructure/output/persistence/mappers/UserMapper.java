package microservice.user_service.users.infrastructure.output.persistence.mappers;

import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.infrastructure.output.persistence.models.UserModel;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper {
    User toDomain(UserModel userModel);
    UserModel toModel(User user);
}
