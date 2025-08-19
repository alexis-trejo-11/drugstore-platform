package microservice.auth.app.core.ports.input;

import microservice.auth.app.application.events.UserLoginEvent;
import microservice.auth.app.application.events.UserRegisteredEvent;

public interface EventPublisher {
    void publishUserRegistered(UserRegisteredEvent event);
    void publishUserLogin(UserLoginEvent event);
}
