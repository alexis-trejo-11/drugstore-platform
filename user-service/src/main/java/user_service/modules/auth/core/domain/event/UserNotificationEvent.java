package user_service.modules.auth.core.domain.event;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.context.ApplicationEvent;
import lombok.Getter;

@Getter
public class UserNotificationEvent extends ApplicationEvent {
    private final User user;
    private NotificationChannel channel;
    private NotificationType type;

    public UserNotificationEvent(Object source, User user, NotificationChannel channel, NotificationType type) {
        super(source);
        this.user = user;
        this.channel = channel;
        this.type = type;
    }

    enum NotificationChannel {
        EMAIL,
        SMS,
        PUSH_NOTIFICATION
    }

    enum NotificationType {
        WELCOME,
        ANNOUCEMENT,
        PASSWORD_RESET,
        ACCOUNT_VERIFICATION
    }

}