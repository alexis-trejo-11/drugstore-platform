package microservice.users.core.domain.events;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserCreatedEvent extends DomainEvent {
    private final UUID userId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;

    public UserCreatedEvent(UUID userId, String firstName, String lastName, String email, String phoneNumber) {
        super();
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
