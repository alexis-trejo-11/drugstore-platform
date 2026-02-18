package user_service.modules.users.core.ports.output;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.modules.users.core.domain.models.enums.UserStatus;
import user_service.modules.users.core.domain.models.valueobjects.Email;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

public interface UserRepository {
  Optional<User> findById(UserId id);

  Optional<User> findByEmail(Email email);

  Optional<User> findByPhoneNumber(PhoneNumber phoneNumber);

  Page<User> ListByRole(UserRole role, Pageable pageInput);

  Page<User> ListByStatus(UserStatus status, Pageable pageInput);

  Page<User> search(String searchCriteriaJSON, Pageable pageInput);

  boolean existsByEmail(Email email);

  boolean existsByPhoneNumber(PhoneNumber phoneNumber);

  boolean existsById(UserId id);

  void updateLastLoginAsync(UserId id);

  User save(User user);

  void deleteById(UserId id);
}
