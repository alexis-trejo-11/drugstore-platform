package microservice.user_service.users.core.ports.output;

import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.utils.page.Page;
import microservice.user_service.utils.page.PageInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String email);
    Page<User> ListByRole(String role, PageInput pageInput);
    Page<User> ListByStatus(String status, PageInput pageInput);
    Page<User> search(String searchCriteriaJSON, PageInput pageInput);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsById(UUID id);
    void updateLastLogin(UUID id);

    User save(User user);
    List<User> findAll();
    void deleteById(UUID id);
}

