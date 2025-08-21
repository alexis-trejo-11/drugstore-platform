package microservice.user_service.auth.core.domain.event;

import lombok.Getter;
import microservice.user_service.users.core.domain.events.DomainEvent;

@Getter
public class UserRegisteredEvent extends DomainEvent {
    private final String userId;
    private final String email;

    public UserRegisteredEvent(String userId, String email) {
        super();
        this.userId = userId;
        this.email = email;
    }
}
