package microservice.auth.app.core.ports.output;


import microservice.auth.app.core.models.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    User save(User user);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
