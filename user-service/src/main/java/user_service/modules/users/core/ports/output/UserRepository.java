package user_service.modules.users.core.ports.output;

import java.util.Optional;

import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.modules.users.core.domain.models.enums.UserStatus;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;
import user_service.modules.users.core.domain.models.valueobjects.Email;
import user_service.modules.users.core.domain.models.valueobjects.UserId;
import user_service.utils.page.PageInput;
import user_service.utils.page.PageResponse;

public interface UserRepository {
    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    Optional<User> findByPhoneNumber(PhoneNumber phoneNumber);

    PageResponse<User> ListByRole(UserRole role, PageInput pageInput);

    PageResponse<User> ListByStatus(UserStatus status, PageInput pageInput);

    PageResponse<User> search(String searchCriteriaJSON, PageInput pageInput);

    boolean existsByEmail(Email email);

    boolean existsByPhoneNumber(PhoneNumber phoneNumber);

    boolean existsById(UserId id);

    void updateLastLoginAsync(UserId id);

    User save(User user);

    void deleteById(UserId id);
}
