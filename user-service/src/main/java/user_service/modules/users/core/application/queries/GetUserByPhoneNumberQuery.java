package user_service.modules.users.core.application.queries;

import user_service.modules.users.core.application.result.UserQueryResult;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;

public record GetUserByPhoneNumberQuery(PhoneNumber phoneNumber) implements Query<UserQueryResult> {
  public GetUserByPhoneNumberQuery {
    if (phoneNumber == null) {
      throw new IllegalArgumentException("Phone Number cannot be null");
    }
  }
}
