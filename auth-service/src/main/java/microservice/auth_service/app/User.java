package microservice.auth_service.app;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import microservice.auth_service.app.auth.core.domain.valueobjects.Email;
import microservice.auth_service.app.auth.core.domain.valueobjects.PhoneNumber;
import microservice.auth_service.app.auth.core.domain.valueobjects.UserId;
import microservice.auth_service.app.auth.core.domain.valueobjects.UserRole;

@Getter
@Setter
@Builder
public class User {
	private UserId id;
	private Email email;
	private String firstName;
	private String lastName;
	private PhoneNumber phoneNumber;
	private String password;
	private UserRole role;
	private LocalDateTime joinedAt;
	private LocalDateTime lastLoginAt;
	private Boolean twoFactorEnabled;

	public static User create(Email email, PhoneNumber phoneNumber, String password, UserRole role) {
		return User.builder()
				.id(UserId.generate())
				.email(email)
				.phoneNumber(phoneNumber)
				.password(password)
				.role(role)
				.joinedAt(LocalDateTime.now())
				.twoFactorEnabled(false)
				.build();
	}

	public void updateEmail(Email newEmail) {
		this.email = newEmail;
	}

	public void changePassword(String newPassword) {
		this.password = newPassword;
	}
}
