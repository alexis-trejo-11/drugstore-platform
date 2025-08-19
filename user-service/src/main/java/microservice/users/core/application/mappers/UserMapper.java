package microservice.users.core.application.mappers;

import microservice.users.core.application.command.CreateUserCommand;
import microservice.users.core.domain.events.UserCreatedEvent;
import microservice.users.core.domain.models.entities.User;
import microservice.users.core.domain.models.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

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
}