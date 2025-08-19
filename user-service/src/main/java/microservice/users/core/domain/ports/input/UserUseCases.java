package microservice.users.core.domain.ports.input;

import microservice.users.core.domain.models.valueobjects.Email;
import microservice.users.core.domain.models.valueobjects.Password;
import microservice.users.core.domain.models.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

public interface UserUseCases {
    User createUser(User user);
    Optional<User> getUserById(UserId id);
    Optional<User> getUserByEmail(Email email);
    List<User> getAllUsers();
    User updateUser(User user);
    void deleteUser(UserId id);
    boolean changePassword(UserId id, Password oldPassword, Password newPassword);
    boolean authenticateUser(Email email, Password password);
}
