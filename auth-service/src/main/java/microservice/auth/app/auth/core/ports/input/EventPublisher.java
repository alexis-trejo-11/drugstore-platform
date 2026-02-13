package microservice.auth.app.auth.core.ports.input;

import microservice.auth.app.auth.core.application.events.UserLoginEvent;
import microservice.auth.app.auth.core.application.events.UserRegisteredEvent;

public interface EventPublisher {
    void publishUserRegistered(UserRegisteredEvent event);
    void publishUserLogin(UserLoginEvent event);
}
