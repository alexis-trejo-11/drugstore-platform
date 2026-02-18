package user_service.modules.users.core.application.mappers;

import user_service.modules.users.core.application.command.CreateUserCommand;
import user_service.modules.users.core.domain.events.UserCreatedEvent;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.utils.page.PaginationMetadata;

import org.springframework.stereotype.Component;

import java.util.List;

import user_service.modules.users.core.application.result.UserPaginatedResponse;
import user_service.modules.users.core.application.result.UserResponse;
import user_service.modules.users.core.domain.models.entities.CreateUserParams;
import user_service.utils.page.PageResponse;

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

  public UserCreatedEvent ToEvent(User user) {
    return new UserCreatedEvent(
        user.getId() != null ? user.getId().value() : null,
        user.getEmail() != null ? user.getEmail().value() : null,
        user.getPhoneNumber() != null ? user.getPhoneNumber().value() : null);
  }

  public UserResponse toResponse(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .phoneNumber(user.getPhoneNumber())
        .role(user.getRole())
        .status(user.getStatus())
        .createdAt(user.getCreatedAt())
        .lastLoginAt(user.getUpdatedAt())
        .build();
  }

  public UserPaginatedResponse toPaginatedResponse(PageResponse<User> userPage) {
    if (userPage == null || userPage.getContent() == null || userPage.getContent().isEmpty()) {
      return new UserPaginatedResponse(List.of(), PaginationMetadata.empty());
    }

    List<UserResponse> userResponses = userPage.getContent().stream()
        .map(this::toResponse)
        .toList();
    return new UserPaginatedResponse(userResponses, PaginationMetadata.from(userPage));
  }
}