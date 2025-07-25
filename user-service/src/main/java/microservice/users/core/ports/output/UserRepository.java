package microservice.users.core.ports.output;

import microservice.users.core.models.User;
import microservice.users.core.models.valueobjects.UserId;

public interface UserRepository extends CommonRepository<User, UserId> {
}
