package user_service.modules.users.core.domain.events;

import lombok.Getter;

@Getter
public class UserCreatedEvent extends DomainEvent {
  private final String userId;
  private final String email;
  private final String phoneNumber;

  public UserCreatedEvent(String userId, String email, String phoneNumber) {
    super();
    this.userId = userId;
    this.email = email;
    this.phoneNumber = phoneNumber;
  }
}
