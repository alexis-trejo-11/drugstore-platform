package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.UserLoginEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.auth.UserRegisteredEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.user.UserCreatedEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.user.UserDeletedEvent;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.event.user.UserUpdatedEvent;

public interface UserEventPublisher {
  void publishUserRegistered(UserRegisteredEvent event);

  void publishUserLogin(UserLoginEvent event);

  boolean publishUserCreated(UserCreatedEvent event);

  boolean publishUserUpdated(UserUpdatedEvent event);

  boolean publishUserDeleted(UserDeletedEvent event);

}
