package user_service.modules.auth.core.domain.event;

import lombok.Getter;
import user_service.modules.users.core.domain.events.DomainEvent;

@Getter
public class TwoFactorEvent extends DomainEvent {
    private final String userId;
    private final TwoFactorEventType eventType;

    private TwoFactorEvent(String userId, TwoFactorEventType eventType) {
        super();
        this.userId = userId;
        this.eventType = eventType;
    }

    enum TwoFactorEventType {
        ENABLED,
        DISABLED,
        VERIFIED
    }

    public static TwoFactorEvent Enable(String userId) {
        return new TwoFactorEvent(userId, TwoFactorEventType.ENABLED);
    }

    public static TwoFactorEvent Disable(String userId) {
        return new TwoFactorEvent(userId, TwoFactorEventType.DISABLED);
    }

    public static TwoFactorEvent Verify(String userId) {
        return new TwoFactorEvent(userId, TwoFactorEventType.VERIFIED);
    }
}
