package user_service.modules.users.core.ports.output;

import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.modules.users.core.domain.models.enums.UserStatus;
import user_service.utils.page.PageInput;
import user_service.utils.page.PageResponse;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String email);

    PageResponse<User> ListByRole(UserRole role, PageInput pageInput);

    PageResponse<User> ListByStatus(UserStatus status, PageInput pageInput);

    PageResponse<User> search(String searchCriteriaJSON, PageInput pageInput);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsById(UUID id);

    void updateLastLoginAsync(UUID id);

    User save(User user);

    void deleteById(UUID id);
}
