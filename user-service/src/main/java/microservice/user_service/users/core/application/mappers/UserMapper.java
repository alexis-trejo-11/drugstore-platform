package microservice.user_service.users.core.application.mappers;

import microservice.user_service.users.core.application.command.CreateUserCommand;
import microservice.user_service.users.core.application.dto.UserPaginatedResponse;
import microservice.user_service.users.core.application.dto.UserResponse;
import microservice.user_service.users.core.domain.events.UserCreatedEvent;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.domain.models.enums.UserStatus;
import microservice.user_service.utils.page.Page;
import microservice.user_service.utils.page.PaginationMetadata;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

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
        return new User.Builder()
                .email(command.email())
                .phoneNumber(command.phoneNumber())
                .firstName(command.firstName())
                .lastName(command.lastName())
                .role(command.role())
                .hashedPassword(hashedPassword)

                // Generated fields
                .id(UUID.randomUUID())
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public UserCreatedEvent ToEvent(User user) {
        return new UserCreatedEvent(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .joinedAt(user.getCreatedAt().format(DateTimeFormatter.BASIC_ISO_DATE))
                .lastLoginAt(user.getUpdatedAt().format(DateTimeFormatter.BASIC_ISO_DATE))
                .build();
    }

    public UserPaginatedResponse toPaginatedResponse(Page<User> userPage) {
        if (userPage == null || userPage.content() == null || userPage.content().isEmpty()) {
            return new UserPaginatedResponse(List.of(), PaginationMetadata.empty());
        }

        List<UserResponse> userResponses = userPage.content().stream()
                .map(this::toResponse)
                .toList();
        return new UserPaginatedResponse(userResponses, userPage.pageMetadata());
    }
}