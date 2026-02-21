package io.github.alexisTrejo11.drugstore.users.user.core.application.mappers;

import io.github.alexisTrejo11.drugstore.users.user.core.application.command.CreateUserCommand;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.events.UserCreatedEvent;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities.User;

import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.users.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities.CreateUserParams;

@Component
public class UserMapper {

  /**
   * Maps a CreateUserCommand to a User domain entity using the Builder pattern.
   * This method is responsible for setting all required fields for a new user,
   * including generating a unique ID and setting default values.
   *
   * @param command The command to create a new user.
   * @return a new User domain entity.
   */
  public User fromCreateCommand(CreateUserCommand command, String hashedPassword) {
    var createUserParams = CreateUserParams.builder()
        .email(command.email())
        .fullName(command.fullName())
        .phoneNumber(command.phoneNumber())
        .hashedPassword(hashedPassword)
        .role(command.role())
        .twoFactorEnabled(false)
        .build();

    return User.createUser(createUserParams);
  }

  public UserCreatedEvent toEvent(User user) {
    return new UserCreatedEvent(
        user.getId() != null ? user.getId().value() : null,
        user.getEmail() != null ? user.getEmail().value() : null,
        user.getPhoneNumber() != null ? user.getPhoneNumber().value() : null);
  }

  public UserQueryResult toUserQueryResult(User user) {
    return UserQueryResult.builder()
        .id(user.getId())
        .email(user.getEmail())
        .phoneNumber(user.getPhoneNumber())
        .role(user.getRole())
        .status(user.getStatus())
        .createdAt(user.getCreatedAt())
        .lastLoginAt(user.getLastLoginAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }
}