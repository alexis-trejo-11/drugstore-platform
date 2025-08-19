package microservice.users.core.domain.ports.output;

import microservice.users.core.domain.models.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    User save(User user);
    List<User> findAll();
    void delete(UUID id);
}

