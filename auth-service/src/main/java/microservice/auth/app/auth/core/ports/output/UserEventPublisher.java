package microservice.auth.app.auth.core.ports.input;

import microservice.auth.app.auth.core.application.events.UserLoginEvent;
import microservice.auth.app.auth.core.application.events.UserRegisteredEvent;
import microservice.auth.app.auth.core.domain.event.user.UserCreatedEvent;
import microservice.auth.app.auth.core.domain.event.user.UserDeletedEvent;
import microservice.auth.app.auth.core.domain.event.user.UserUpdatedEvent;

public interface UserEventPublisher {
  void publishUserRegistered(UserRegisteredEvent event);

  void publishUserLogin(UserLoginEvent event);

  boolean publishUserCreated(UserCreatedEvent event);

  boolean publishUserUpdated(UserUpdatedEvent event);

  boolean publishUserDeleted(UserDeletedEvent event);

}
