package io.github.alexisTrejo11.drugstore.users.user.core.application.queries;

import io.github.alexisTrejo11.drugstore.users.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.PhoneNumber;

public record GetUserByPhoneNumberQuery(PhoneNumber phoneNumber) implements Query<UserQueryResult> {
  public GetUserByPhoneNumberQuery {
    if (phoneNumber == null) {
      throw new IllegalArgumentException("Phone Number cannot be null");
    }
  }
}
