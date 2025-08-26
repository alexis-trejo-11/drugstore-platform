package user_service.modules.users.core.domain.events;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserCreatedEvent extends DomainEvent {
    private final UUID userId;
    private final String email;
    private final String phoneNumber;

    public UserCreatedEvent(UUID userId, String email, String phoneNumber) {
        super();
        this.userId = userId;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
