package microservice.auth_service.app.auth.core.ports.output;

import microservice.auth_service.app.User;

public interface UserServiceClient {
	boolean isEmailUnique(String email);

	boolean isPhoneUnique(String phone);

	void validateUserCredentials(String email, String password);

	boolean isUserExists(String email);

	User getUserByEmail(String email);

	User getUserByPhone(String phone);

	User getUserById(String userId);

}
