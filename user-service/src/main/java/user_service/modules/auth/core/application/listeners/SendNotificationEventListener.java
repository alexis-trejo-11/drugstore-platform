package user_service.modules.auth.core.application.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import user_service.modules.auth.core.domain.event.UserNotificationEvent;

public class SendNotificationEventListener {

    @EventListener
    @Async("taskExecutor")
    void handleEvent(UserNotificationEvent event) {
        System.out.println("Sending notification to user: " + event.getUser().getName());
    }
}
