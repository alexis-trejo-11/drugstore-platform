package microservice.users.core.models;

import lombok.Builder;
import lombok.Getter;
import microservice.users.core.models.enums.UserRole;
import microservice.users.core.models.valueobjects.Email;
import microservice.users.core.models.valueobjects.Password;
import microservice.users.core.models.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
public abstract class User {
    private UserId id;
    private Email email;
    private Password password;
    private UserRole role;
    private LocalDateTime joinedAt;
    private LocalDateTime lastLoginAt;

    protected User(UserId id, Email email, Password password, UserRole role) {
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email);
        this.password = Objects.requireNonNull(password);
        this.role = Objects.requireNonNull(role);
        this.joinedAt = LocalDateTime.now();
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateEmail(Email newEmail) {
        this.email = newEmail;
    }

    public void changePassword(Password newPassword) {
        this.password = newPassword;
    }
}
