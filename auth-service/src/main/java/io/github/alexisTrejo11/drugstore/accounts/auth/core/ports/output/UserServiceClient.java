package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.User;

public interface UserServiceClient {
  boolean isEmailUnique(String email);

  boolean isPhoneUnique(String phone);

  void validateUserCredentials(String email, String password);

  boolean isUserExists(String email);

  User getUserByEmail(String email);

  User getUserByPhone(String phone);

  User getUserById(String userId);

  /**
   * Persists a new user in the user service (hashed password must already be encoded).
   */
  User createUser(
      String email,
      String phone,
      String firstName,
      String lastName,
      String hashedPassword,
      String roleName);

  void activateUser(String userId);

  void updateUserPassword(String userId, String hashedPassword);

  /**
   * Updates email and/or phone when non-blank; unchanged fields pass current values from caller.
   */
  User updateUserCredentials(String userId, String email, String phone);

  void completeTwoFactorEnrollment(String userId, String totpSecretBase32);

  void disableTwoFactorOnUser(String userId);
}
