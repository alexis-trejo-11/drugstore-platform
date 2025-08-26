package user_service.modules.auth.core.domain.event;

import lombok.Getter;
import user_service.modules.users.core.domain.events.DomainEvent;

@Getter
public class PasswordEvent extends DomainEvent {
    private final String userId;
    private final String password;
    private final String eventType;
    private final String email;

    private PasswordEvent(String userId, String password, String eventType, String email) {
        this.userId = userId;
        this.password = password;
        this.eventType = eventType;
        this.email = email;
    }

    enum PasswordEventType {
        PASSWORD_CHANGED,
        PASSWORD_RESET
    }

    public static PasswordEvent passwordChanged(String userId, String password, String email) {
        return new PasswordEvent(userId, password, PasswordEventType.PASSWORD_CHANGED.name(), email);
    }

    public static PasswordEvent passwordReset(String userId, String password, String email) {
        return new PasswordEvent(userId, password, PasswordEventType.PASSWORD_RESET.name(), email);
    }
}
