package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.grpc;

import java.util.concurrent.TimeUnit;

import com.microservices.grpc.user.*;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.InvalidCredentialsException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.UserNotFoundException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.UserServiceAuthException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.UserServiceException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.UserServiceTimeoutException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.UserServiceUnavailableException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.UserAlreadyExistsError;


@Component
public class UserServiceGrpcClient implements UserServiceClient {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserServiceGrpcClient.class);
  private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;
  private final UserGrpcMapper userGrpcMapper;

  // Timeouts per operation (in seconds)
  private static final long DEFAULT_TIMEOUT = 5L;
  private static final long VALIDATION_TIMEOUT = 3L;
  private static final long QUERY_TIMEOUT = 2L;

  @Autowired
  public UserServiceGrpcClient(ManagedChannel userServiceChannel, UserGrpcMapper mapper) {
    this.userServiceStub = UserServiceGrpc.newBlockingStub(userServiceChannel);
    this.userGrpcMapper = mapper;
  }

  @Override
  public boolean isEmailUnique(String email) {
    log.debug("Checking if email is unique: {}", email);
    try {
      EmailRequest request = EmailRequest.newBuilder()
          .setEmail(email)
          .build();

      BoolResponse response = userServiceStub
          .withDeadlineAfter(QUERY_TIMEOUT, TimeUnit.SECONDS)
          .isEmailUnique(request);

      log.debug("Email uniqueness check result: {}", response.getValue());
      return response.getValue();

    } catch (StatusRuntimeException e) {
      throw handleGrpcException("isEmailUnique", e);
    }
  }

  @Override
  public boolean isPhoneUnique(String phone) {
    log.debug("Checking if phone is unique: {}", phone);
    try {
      PhoneNumberRequest request = PhoneNumberRequest.newBuilder()
          .setPhoneNumber(phone)
          .build();

      BoolResponse response = userServiceStub
          .withDeadlineAfter(QUERY_TIMEOUT, TimeUnit.SECONDS)
          .isPhoneNumberUnique(request);

      log.debug("Phone uniqueness check result: {}", response.getValue());
      return response.getValue();

    } catch (StatusRuntimeException e) {
      throw handleGrpcException("isPhoneUnique", e);
    }
  }

  @Override
  public void validateUserCredentials(String email, String password) {
    log.debug("Validating credentials for email: {}", email);
    try {
      ValidateUserCredentialsRequest request = ValidateUserCredentialsRequest.newBuilder()
          .setEmail(email)
          .setPassword(password)
          .build();

      ValidateUserCredentialsResponse response = userServiceStub
          .withDeadlineAfter(VALIDATION_TIMEOUT, TimeUnit.SECONDS)
          .validateUserCredentials(request);

      if (!response.getIsValid()) {
        log.warn("Invalid credentials for email: {}", email);
        throw new InvalidCredentialsException(response.getMessage());
      }

      log.debug("Credentials validated successfully for email: {}", email);

    } catch (StatusRuntimeException e) {
      throw handleGrpcException("validateUserCredentials", e);
    }
  }

  @Override
  public boolean isUserExists(String email) {
    log.debug("Checking if user exists: {}", email);
    try {
      EmailRequest request = EmailRequest.newBuilder()
          .setEmail(email)
          .build();

      BoolResponse response = userServiceStub
          .withDeadlineAfter(QUERY_TIMEOUT, TimeUnit.SECONDS)
          .isUserExistsByEmail(request);

      log.debug("User existence check result: {}", response.getValue());
      return response.getValue();

    } catch (StatusRuntimeException e) {
      throw handleGrpcException("isUserExists", e);
    }
  }

  @Override
  public User getUserByEmail(String email) {
    log.debug("Getting user by email: {}", email);
    try {
      EmailRequest request = EmailRequest.newBuilder()
          .setEmail(email)
          .build();

      UserResponse response = userServiceStub
          .withDeadlineAfter(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
          .getUserByEmail(request);

      User user = userGrpcMapper.toDomain(response);
      log.debug("User retrieved successfully: {}", user.getId());
      return user;

    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
        log.debug("User not found with email: {}", email);
        return null;
      }
      throw handleGrpcException("getUserByEmail", e);
    }
  }

  @Override
  public User getUserByPhone(String phone) {
    log.debug("Getting user by phone: {}", phone);
    try {
      PhoneNumberRequest request = PhoneNumberRequest.newBuilder()
          .setPhoneNumber(phone)
          .build();

      UserResponse response = userServiceStub
          .withDeadlineAfter(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
          .getUserByPhone(request);

      User user = userGrpcMapper.toDomain(response);
      log.debug("User retrieved successfully: {}", user.getId());
      return user;

    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
        log.debug("User not found with phone: {}", phone);
        return null;
      }
      throw handleGrpcException("getUserByPhone", e);
    }
  }

  @Override
  public User getUserById(String userId) {
    log.debug("Getting user by ID: {}", userId);
    try {
      UserIdRequest request = UserIdRequest.newBuilder()
          .setUserId(userId)
          .build();

      UserResponse response = userServiceStub
          .withDeadlineAfter(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
          .getUserById(request);

      User user = userGrpcMapper.toDomain(response);
      log.debug("User retrieved successfully: {}", user.getId());
      return user;

    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
        log.debug("User not found with ID: {}", userId);
        return null;
      }
      throw handleGrpcException("getUserById", e);
    }
  }

  @Override
  public User createUser(
      String email,
      String phone,
      String firstName,
      String lastName,
      String hashedPassword,
      String roleName) {
    log.debug("Creating user with email: {}", email);
    try {
      CreateUserRequest request = CreateUserRequest.newBuilder()
          .setEmail(email)
          .setPhoneNumber(phone)
          .setFirstName(firstName)
          .setLastName(lastName)
          .setHashedPassword(hashedPassword)
          .setRole(roleName)
          .build();

      UserResponse response = userServiceStub
          .withDeadlineAfter(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
          .createUser(request);

      return userGrpcMapper.toDomain(response);
    } catch (StatusRuntimeException e) {
      throw handleGrpcException("createUser", e);
    }
  }

  @Override
  public void activateUser(String userId) {
    log.debug("Activating user: {}", userId);
    try {
      UserIdRequest request = UserIdRequest.newBuilder().setUserId(userId).build();
      userServiceStub.withDeadlineAfter(DEFAULT_TIMEOUT, TimeUnit.SECONDS).activateUser(request);
    } catch (StatusRuntimeException e) {
      throw handleGrpcException("activateUser", e);
    }
  }

  @Override
  public void updateUserPassword(String userId, String hashedPassword) {
    log.debug("Updating password for user: {}", userId);
    try {
      UpdateUserPasswordRequest request = UpdateUserPasswordRequest.newBuilder()
          .setUserId(userId)
          .setHashedPassword(hashedPassword)
          .build();
      userServiceStub.withDeadlineAfter(DEFAULT_TIMEOUT, TimeUnit.SECONDS).updateUserPassword(request);
    } catch (StatusRuntimeException e) {
      throw handleGrpcException("updateUserPassword", e);
    }
  }

  @Override
  public User updateUserCredentials(String userId, String email, String phone) {
    log.debug("Updating credentials for user: {}", userId);
    try {
      UpdateUserCredentialsRequest request = UpdateUserCredentialsRequest.newBuilder()
          .setUserId(userId)
          .setEmail(email != null ? email : "")
          .setPhoneNumber(phone != null ? phone : "")
          .build();

      UserResponse response = userServiceStub
          .withDeadlineAfter(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
          .updateUserCredentials(request);

      return userGrpcMapper.toDomain(response);
    } catch (StatusRuntimeException e) {
      throw handleGrpcException("updateUserCredentials", e);
    }
  }

  @Override
  public void completeTwoFactorEnrollment(String userId, String totpSecretBase32) {
    log.debug("Completing 2FA enrollment for user: {}", userId);
    try {
      TwoFactorEnrollmentRequest request = TwoFactorEnrollmentRequest.newBuilder()
          .setUserId(userId)
          .setTotpSecretBase32(totpSecretBase32)
          .build();
      userServiceStub.withDeadlineAfter(DEFAULT_TIMEOUT, TimeUnit.SECONDS).completeTwoFactorEnrollment(request);
    } catch (StatusRuntimeException e) {
      throw handleGrpcException("completeTwoFactorEnrollment", e);
    }
  }

  @Override
  public void disableTwoFactorOnUser(String userId) {
    log.debug("Disabling 2FA for user: {}", userId);
    try {
      UserIdRequest request = UserIdRequest.newBuilder().setUserId(userId).build();
      userServiceStub.withDeadlineAfter(DEFAULT_TIMEOUT, TimeUnit.SECONDS).disableTwoFactorAuth(request);
    } catch (StatusRuntimeException e) {
      throw handleGrpcException("disableTwoFactorOnUser", e);
    }
  }

  private RuntimeException handleGrpcException(String operation, StatusRuntimeException e) {
    Status.Code code = e.getStatus().getCode();
    String description = e.getStatus().getDescription();

    log.error("gRPC error in operation '{}': {} - {}", operation, code, description, e);

    switch (code) {
      case NOT_FOUND -> throw new UserNotFoundException("User not found: " + description);

      case ALREADY_EXISTS ->
          throw new UserAlreadyExistsError(description != null ? description : "Resource already exists");

      case FAILED_PRECONDITION -> throw new IllegalStateException(description != null ? description : "Precondition failed");

      case INVALID_ARGUMENT -> throw new IllegalArgumentException("Invalid argument: " + description);

      case DEADLINE_EXCEEDED -> throw new UserServiceTimeoutException(
          "Timeout calling User Service operation: " + operation);

      case UNAVAILABLE -> throw new UserServiceUnavailableException(
          "User Service is unavailable: " + description);

      case UNAUTHENTICATED, PERMISSION_DENIED -> throw new UserServiceAuthException(
          "Authentication/Authorization error: " + description);
      default -> throw new UserServiceException(
          "Error calling User Service: " + operation + " - " + description, e);
    }
  }
}
