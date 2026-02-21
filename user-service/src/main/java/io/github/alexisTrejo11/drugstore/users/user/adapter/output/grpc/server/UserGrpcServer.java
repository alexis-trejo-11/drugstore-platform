package io.github.alexisTrejo11.drugstore.users.user.adapter.output.grpc.server;

import io.github.alexisTrejo11.drugstore.users.user.adapter.password.PasswordEncoder;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.output.UserRepository;
import io.grpc.stub.StreamObserver;
import com.microservices.grpc.user.*;
import com.microservices.grpc.user.UserServiceGrpc.UserServiceImplBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class UserGrpcServer extends UserServiceImplBase {
	private static final Logger logger = LoggerFactory.getLogger(UserGrpcServer.class);
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserGrpcServer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void isEmailUnique(EmailRequest request, StreamObserver<BoolResponse> responseObserver) {
		logger.debug("Checking if email is unique: {}", request.getEmail());
		try {
			Email email = new Email(request.getEmail());
			boolean exists = userRepository.existsByEmail(email);
			boolean isUnique = !exists;

			BoolResponse response = BoolResponse.newBuilder()
					.setValue(isUnique)
					.setMessage(isUnique ? "Email is unique and available" : "Email already exists in the system")
					.build();

			logger.info("Email uniqueness check completed for: {} - Result: {}", request.getEmail(), isUnique);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (Exception e) {
			logger.error("Error checking email uniqueness for: {}", request.getEmail(), e);
			responseObserver.onError(io.grpc.Status.INTERNAL
					.withDescription("Error checking email uniqueness: " + e.getMessage())
					.asException());
		}
	}

	@Override
	public void isPhoneNumberUnique(PhoneNumberRequest request, StreamObserver<BoolResponse> responseObserver) {
		logger.debug("Checking if phone number is unique: {}", request.getPhoneNumber());
		try {
			PhoneNumber phoneNumber = new PhoneNumber(request.getPhoneNumber());
			boolean exists = userRepository.existsByPhoneNumber(phoneNumber);
			boolean isUnique = !exists;

			BoolResponse response = BoolResponse.newBuilder()
					.setValue(isUnique)
					.setMessage(isUnique ? "Phone number is unique and available" : "Phone number already exists in the system")
					.build();

			logger.info("Phone number uniqueness check completed for: {} - Result: {}", request.getPhoneNumber(), isUnique);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (Exception e) {
			logger.error("Error checking phone number uniqueness for: {}", request.getPhoneNumber(), e);
			responseObserver.onError(io.grpc.Status.INTERNAL
					.withDescription("Error checking phone number uniqueness: " + e.getMessage())
					.asException());
		}
	}

	@Override
	public void isUserExistsByEmail(EmailRequest request, StreamObserver<BoolResponse> responseObserver) {
		logger.debug("Checking if user exists by email: {}", request.getEmail());
		try {
			Email email = new Email(request.getEmail());
			Optional<User> user = userRepository.findByEmail(email);
			boolean exists = user.isPresent();

			BoolResponse response = BoolResponse.newBuilder()
					.setValue(exists)
					.setMessage(exists ? "User found with provided email" : "No user found with provided email")
					.build();

			logger.info("User existence check by email completed for: {} - Result: {}", request.getEmail(), exists);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (Exception e) {
			logger.error("Error checking user existence by email for: {}", request.getEmail(), e);
			responseObserver.onError(io.grpc.Status.INTERNAL
					.withDescription("Error checking user existence: " + e.getMessage())
					.asException());
		}
	}

	@Override
	public void isUserExistsByPhoneNumber(PhoneNumberRequest request, StreamObserver<BoolResponse> responseObserver) {
		logger.debug("Checking if user exists by phone number: {}", request.getPhoneNumber());
		try {
			PhoneNumber phoneNumber = new PhoneNumber(request.getPhoneNumber());
			Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
			boolean exists = user.isPresent();

			BoolResponse response = BoolResponse.newBuilder()
					.setValue(exists)
					.setMessage(exists ? "User found with provided phone number" : "No user found with provided phone number")
					.build();

			logger.info("User existence check by phone number completed for: {} - Result: {}", request.getPhoneNumber(), exists);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (Exception e) {
			logger.error("Error checking user existence by phone number for: {}", request.getPhoneNumber(), e);
			responseObserver.onError(io.grpc.Status.INTERNAL
					.withDescription("Error checking user existence: " + e.getMessage())
					.asException());
		}
	}

	@Override
	public void validateUserCredentials(ValidateUserCredentialsRequest request,
	                                    StreamObserver<ValidateUserCredentialsResponse> responseObserver) {
		logger.debug("Validating user credentials for email: {}", request.getEmail());
		try {
			Email email = new Email(request.getEmail());
			Optional<User> userOptional = userRepository.findByEmail(email);

			if (userOptional.isEmpty()) {
				logger.warn("Authentication attempt with non-existent email: {}", request.getEmail());
				ValidateUserCredentialsResponse response = ValidateUserCredentialsResponse.newBuilder()
						.setIsValid(false)
						.setUserId("")
						.setMessage("Invalid email or password")
						.build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
				return;
			}

			User user = userOptional.get();
			user.validateNotDisabled();

			boolean passwordMatches = passwordEncoder.verifyPassword(request.getPassword(), user.getHashedPassword());

			if (!passwordMatches) {
				logger.warn("Failed authentication attempt for user: {}", user.getId());
				ValidateUserCredentialsResponse response = ValidateUserCredentialsResponse.newBuilder()
						.setIsValid(false)
						.setUserId("")
						.setMessage("Invalid email or password")
						.build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
				return;
			}

			user.updateLastLogin();
			userRepository.updateLastLoginAsync(user.getId());

			logger.info("Successful authentication for user: {}", user.getId());
			ValidateUserCredentialsResponse response = ValidateUserCredentialsResponse.newBuilder()
					.setIsValid(true)
					.setUserId(user.getId().value())
					.setMessage("Authentication successful")
					.build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (Exception e) {
			logger.error("Error validating user credentials for email: {}", request.getEmail(), e);
			responseObserver.onError(io.grpc.Status.INTERNAL
					.withDescription("Error validating credentials: " + e.getMessage())
					.asException());
		}
	}

	@Override
	public void getUserByEmail(EmailRequest request, StreamObserver<UserResponse> responseObserver) {
		logger.debug("Retrieving user by email: {}", request.getEmail());
		try {
			Email email = new Email(request.getEmail());
			Optional<User> userOptional = userRepository.findByEmail(email);

			if (userOptional.isEmpty()) {
				logger.warn("User not found with email: {}", request.getEmail());
				responseObserver.onError(io.grpc.Status.NOT_FOUND
						.withDescription("User not found with provided email")
						.asException());
				return;
			}

			User user = userOptional.get();
			UserResponse response = buildUserResponse(user);

			logger.info("User retrieved successfully by email: {}", user.getId());
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (Exception e) {
			logger.error("Error retrieving user by email: {}", request.getEmail(), e);
			responseObserver.onError(io.grpc.Status.INTERNAL
					.withDescription("Error retrieving user: " + e.getMessage())
					.asException());
		}
	}

	@Override
	public void getUserByPhone(PhoneNumberRequest request, StreamObserver<UserResponse> responseObserver) {
		logger.debug("Retrieving user by phone number: {}", request.getPhoneNumber());
		try {
			PhoneNumber phoneNumber = new PhoneNumber(request.getPhoneNumber());
			Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

			if (userOptional.isEmpty()) {
				logger.warn("User not found with phone number: {}", request.getPhoneNumber());
				responseObserver.onError(io.grpc.Status.NOT_FOUND
						.withDescription("User not found with provided phone number")
						.asException());
				return;
			}

			User user = userOptional.get();
			UserResponse response = buildUserResponse(user);

			logger.info("User retrieved successfully by phone number: {}", user.getId());
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (Exception e) {
			logger.error("Error retrieving user by phone number: {}", request.getPhoneNumber(), e);
			responseObserver.onError(io.grpc.Status.INTERNAL
					.withDescription("Error retrieving user: " + e.getMessage())
					.asException());
		}
	}

	@Override
	public void getUserById(UserIdRequest request, StreamObserver<UserResponse> responseObserver) {
		logger.debug("Retrieving user by ID: {}", request.getUserId());
		try {
			UserId userId = new UserId(request.getUserId());
			Optional<User> userOptional = userRepository.findById(userId);

			if (userOptional.isEmpty()) {
				logger.warn("User not found with ID: {}", request.getUserId());
				responseObserver.onError(io.grpc.Status.NOT_FOUND
						.withDescription("User not found with provided ID")
						.asException());
				return;
			}

			User user = userOptional.get();
			UserResponse response = buildUserResponse(user);

			logger.info("User retrieved successfully by ID: {}", user.getId());
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (Exception e) {
			logger.error("Error retrieving user by ID: {}", request.getUserId(), e);
			responseObserver.onError(io.grpc.Status.INTERNAL
					.withDescription("Error retrieving user: " + e.getMessage())
					.asException());
		}
	}

	/**
	 * Helper method to build a UserResponse from a User entity
	 */
	private UserResponse buildUserResponse(User user) {
		return UserResponse.newBuilder()
				.setUserId(user.getId().value())
				.setEmail(user.getEmail().value())
				.setFirstName(user.getFullName().firstName())
				.setLastName(user.getFullName().lastName())
				.setPhoneNumber(user.getPhoneNumber().value())
				.setRole(user.getRole().name())
				.setTwoFactorEnabled(user.isTwoFactorEnabled())
				.setEmailVerified(user.getStatus().name().equals("ACTIVE"))
				.setStatus(user.getStatus().name())
				.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().format(DATE_FORMATTER) : "")
				.setUpdatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().format(DATE_FORMATTER) : "")
				.build();
	}
}
