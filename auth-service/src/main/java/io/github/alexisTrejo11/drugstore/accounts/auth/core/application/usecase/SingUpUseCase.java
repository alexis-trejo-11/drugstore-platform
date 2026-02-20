package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.SignupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.events.UserRegisteredEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SignUpResult;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.UserAlreadyExistsError;
import io.github.alexisTrejo11.drugstore.accounts.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserEventPublisher;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserRole;

@Service
public class SingUpUseCase {
  private final UserEventPublisher eventPublisher;
  private final UserServiceClient serviceClient;

  @Autowired
  public SingUpUseCase(UserEventPublisher eventPublisher, UserServiceClient serviceClient) {
    this.eventPublisher = eventPublisher;
    this.serviceClient = serviceClient;
  }

  public SignUpResult execute(SignupCommand command) {
    validateUserUniqueness(command);

    User newUser = createUser(command);
    Boolean requiresEmailVerification = !command.role().equals(UserRole.ADMIN);

    publishSignupEvent(newUser);
    return SignUpResult.success(newUser, requiresEmailVerification);
  }

  private void validateUserUniqueness(SignupCommand command) {
    if (!serviceClient.isEmailUnique(command.email().value())) {
      throw new UserAlreadyExistsError("Email already exists: " + command.email());
    }
    if (!serviceClient.isPhoneUnique(command.phone().value())) {
      throw new UserAlreadyExistsError("Phone number already exists: " + command.phone());
    }
  }

  private User createUser(SignupCommand command) {
    return User.builder()
        .email(command.email())
        .phoneNumber(command.phone())
        .password(command.password() != null ? command.password().value() : null)
        .firstName(command.firstName())
        .lastName(command.lastName())
        .role(command.role())
        .build();
  }

  public void publishSignupEvent(User user) {
    UserRegisteredEvent event = new UserRegisteredEvent(
        user.getId(),
        user.getEmail(),
        user.getPhoneNumber());
    eventPublisher.publishUserRegistered(event);
  }
}
