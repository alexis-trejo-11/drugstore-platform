package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output;

import io.github.alexisTrejo11.drugstore.accounts.User;

public interface UserServiceClient {
	boolean isEmailUnique(String email);

	boolean isPhoneUnique(String phone);

	void validateUserCredentials(String email, String password);

	boolean isUserExists(String email);

	User getUserByEmail(String email);

	User getUserByPhone(String phone);

	User getUserById(String userId);

}
