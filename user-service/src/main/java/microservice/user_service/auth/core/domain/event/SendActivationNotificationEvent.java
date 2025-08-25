package microservice.user_service.auth.core.domain.event;

import java.util.UUID;
import org.springframework.context.ApplicationEvent;
import lombok.Getter;

@Getter
public class SendActivationNotificationEvent extends ApplicationEvent {
    private final UUID userId;
    private final String email;
    private final String firstName;
    private final String activationToken;

    public SendActivationNotificationEvent(Object source, UUID userId, String email,
            String firstName, String activationToken) {
        super(source);
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.activationToken = activationToken;
    }
}