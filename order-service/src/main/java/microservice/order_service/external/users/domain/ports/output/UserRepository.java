package microservice.order_service.external.users.domain.ports.output;

import microservice.order_service.external.users.domain.entity.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByID(String userID);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    void delete(User user);
    void restore(User user);
}
