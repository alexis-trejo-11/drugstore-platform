package io.github.alexisTrejo11.drugstore.accounts.integration.support;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.microservices.grpc.user.BoolResponse;
import com.microservices.grpc.user.CreateUserRequest;
import com.microservices.grpc.user.EmailRequest;
import com.microservices.grpc.user.PhoneNumberRequest;
import com.microservices.grpc.user.TwoFactorEnrollmentRequest;
import com.microservices.grpc.user.UpdateUserCredentialsRequest;
import com.microservices.grpc.user.UpdateUserPasswordRequest;
import com.microservices.grpc.user.UserIdRequest;
import com.microservices.grpc.user.UserResponse;
import com.microservices.grpc.user.UserServiceGrpc;
import com.microservices.grpc.user.ValidateUserCredentialsRequest;
import com.microservices.grpc.user.ValidateUserCredentialsResponse;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * In-memory UserService for integration tests. Uses the same BCrypt algorithm as
 * {@link org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder}.
 */
public final class InMemoryUserGrpcServer extends UserServiceGrpc.UserServiceImplBase {

  private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
  private final Map<String, StoredUser> byId = new ConcurrentHashMap<>();
  private final Map<String, StoredUser> byEmail = new ConcurrentHashMap<>();
  private final Map<String, StoredUser> byPhone = new ConcurrentHashMap<>();

  public void clear() {
    byId.clear();
    byEmail.clear();
    byPhone.clear();
  }

  private static String normEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase();
  }

  private static String normPhone(String phone) {
    if (phone == null || phone.isBlank()) {
      return "";
    }
    String digits = phone.replaceAll("[^0-9+]", "");
    if (digits.isEmpty()) {
      return "";
    }
    return digits.startsWith("+") ? digits : "+" + digits;
  }

  private UserResponse toResponse(StoredUser u) {
    return UserResponse.newBuilder()
        .setUserId(u.id)
        .setEmail(u.email)
        .setFirstName(u.firstName)
        .setLastName(u.lastName)
        .setPhoneNumber(u.phone)
        .setRole(u.role)
        .setTwoFactorEnabled(u.twoFactorEnabled)
        .setEmailVerified(true)
        .setStatus(u.status)
        .setCreatedAt(u.createdAt)
        .setUpdatedAt(u.updatedAt)
        .build();
  }

  private void notFound(StreamObserver<?> responseObserver) {
    responseObserver.onError(Status.NOT_FOUND.withDescription("User not found").asRuntimeException());
  }

  @Override
  public void isEmailUnique(EmailRequest request, StreamObserver<BoolResponse> responseObserver) {
    String email = normEmail(request.getEmail());
    boolean unique = !byEmail.containsKey(email);
    responseObserver.onNext(BoolResponse.newBuilder().setValue(unique).setMessage("").build());
    responseObserver.onCompleted();
  }

  @Override
  public void isPhoneNumberUnique(
      PhoneNumberRequest request, StreamObserver<BoolResponse> responseObserver) {
    String phone = normPhone(request.getPhoneNumber());
    boolean unique = phone.isEmpty() || !byPhone.containsKey(phone);
    responseObserver.onNext(BoolResponse.newBuilder().setValue(unique).setMessage("").build());
    responseObserver.onCompleted();
  }

  @Override
  public void isUserExistsByEmail(EmailRequest request, StreamObserver<BoolResponse> responseObserver) {
    boolean exists = byEmail.containsKey(normEmail(request.getEmail()));
    responseObserver.onNext(BoolResponse.newBuilder().setValue(exists).setMessage("").build());
    responseObserver.onCompleted();
  }

  @Override
  public void isUserExistsByPhoneNumber(
      PhoneNumberRequest request, StreamObserver<BoolResponse> responseObserver) {
    boolean exists = byPhone.containsKey(normPhone(request.getPhoneNumber()));
    responseObserver.onNext(BoolResponse.newBuilder().setValue(exists).setMessage("").build());
    responseObserver.onCompleted();
  }

  @Override
  public void validateUserCredentials(
      ValidateUserCredentialsRequest request,
      StreamObserver<ValidateUserCredentialsResponse> responseObserver) {
    StoredUser u = byEmail.get(normEmail(request.getEmail()));
    boolean ok =
        u != null && bcrypt.matches(request.getPassword(), u.hashedPassword);
    ValidateUserCredentialsResponse.Builder b =
        ValidateUserCredentialsResponse.newBuilder().setIsValid(ok);
    if (ok && u != null) {
      b.setUserId(u.id).setMessage("OK");
    } else {
      b.setMessage("Invalid credentials");
    }
    responseObserver.onNext(b.build());
    responseObserver.onCompleted();
  }

  @Override
  public void getUserByEmail(EmailRequest request, StreamObserver<UserResponse> responseObserver) {
    StoredUser u = byEmail.get(normEmail(request.getEmail()));
    if (u == null) {
      notFound(responseObserver);
      return;
    }
    responseObserver.onNext(toResponse(u));
    responseObserver.onCompleted();
  }

  @Override
  public void getUserByPhone(PhoneNumberRequest request, StreamObserver<UserResponse> responseObserver) {
    StoredUser u = byPhone.get(normPhone(request.getPhoneNumber()));
    if (u == null) {
      notFound(responseObserver);
      return;
    }
    responseObserver.onNext(toResponse(u));
    responseObserver.onCompleted();
  }

  @Override
  public void getUserById(UserIdRequest request, StreamObserver<UserResponse> responseObserver) {
    StoredUser u = byId.get(request.getUserId());
    if (u == null) {
      notFound(responseObserver);
      return;
    }
    responseObserver.onNext(toResponse(u));
    responseObserver.onCompleted();
  }

  @Override
  public void createUser(CreateUserRequest request, StreamObserver<UserResponse> responseObserver) {
    String id = UUID.randomUUID().toString();
    String email = normEmail(request.getEmail());
    String phone = normPhone(request.getPhoneNumber());
    String now = ISO.format(LocalDateTime.now());
    StoredUser u = new StoredUser();
    u.id = id;
    u.email = email;
    u.phone = phone;
    u.firstName = request.getFirstName();
    u.lastName = request.getLastName();
    u.hashedPassword = request.getHashedPassword();
    u.role = request.getRole();
    u.status = "PENDING";
    u.twoFactorEnabled = false;
    u.totpSecretBase32 = null;
    u.createdAt = now;
    u.updatedAt = now;
    byId.put(id, u);
    byEmail.put(email, u);
    byPhone.put(phone, u);
    responseObserver.onNext(toResponse(u));
    responseObserver.onCompleted();
  }

  @Override
  public void activateUser(UserIdRequest request, StreamObserver<UserResponse> responseObserver) {
    StoredUser u = byId.get(request.getUserId());
    if (u == null) {
      notFound(responseObserver);
      return;
    }
    u.status = "ACTIVE";
    u.updatedAt = ISO.format(LocalDateTime.now());
    responseObserver.onNext(toResponse(u));
    responseObserver.onCompleted();
  }

  @Override
  public void updateUserPassword(
      UpdateUserPasswordRequest request, StreamObserver<BoolResponse> responseObserver) {
    StoredUser u = byId.get(request.getUserId());
    if (u == null) {
      notFound(responseObserver);
      return;
    }
    u.hashedPassword = request.getHashedPassword();
    u.updatedAt = ISO.format(LocalDateTime.now());
    responseObserver.onNext(BoolResponse.newBuilder().setValue(true).setMessage("").build());
    responseObserver.onCompleted();
  }

  @Override
  public void updateUserCredentials(
      UpdateUserCredentialsRequest request, StreamObserver<UserResponse> responseObserver) {
    StoredUser u = byId.get(request.getUserId());
    if (u == null) {
      notFound(responseObserver);
      return;
    }
    String newEmail = request.getEmail();
    if (newEmail != null && !newEmail.isBlank()) {
      byEmail.remove(u.email);
      u.email = normEmail(newEmail);
      byEmail.put(u.email, u);
    }
    String newPhone = request.getPhoneNumber();
    if (newPhone != null && !newPhone.isBlank()) {
      byPhone.remove(u.phone);
      u.phone = normPhone(newPhone);
      byPhone.put(u.phone, u);
    }
    u.updatedAt = ISO.format(LocalDateTime.now());
    responseObserver.onNext(toResponse(u));
    responseObserver.onCompleted();
  }

  @Override
  public void completeTwoFactorEnrollment(
      TwoFactorEnrollmentRequest request, StreamObserver<BoolResponse> responseObserver) {
    StoredUser u = byId.get(request.getUserId());
    if (u == null) {
      notFound(responseObserver);
      return;
    }
    u.totpSecretBase32 = request.getTotpSecretBase32();
    u.twoFactorEnabled = true;
    u.updatedAt = ISO.format(LocalDateTime.now());
    responseObserver.onNext(BoolResponse.newBuilder().setValue(true).setMessage("").build());
    responseObserver.onCompleted();
  }

  @Override
  public void disableTwoFactorAuth(UserIdRequest request, StreamObserver<BoolResponse> responseObserver) {
    StoredUser u = byId.get(request.getUserId());
    if (u == null) {
      notFound(responseObserver);
      return;
    }
    u.twoFactorEnabled = false;
    u.totpSecretBase32 = null;
    u.updatedAt = ISO.format(LocalDateTime.now());
    responseObserver.onNext(BoolResponse.newBuilder().setValue(true).setMessage("").build());
    responseObserver.onCompleted();
  }

  private static final class StoredUser {
    String id;
    String email;
    String phone;
    String firstName;
    String lastName;
    String hashedPassword;
    String role;
    String status;
    boolean twoFactorEnabled;
    String totpSecretBase32;
    String createdAt;
    String updatedAt;
  }
}
