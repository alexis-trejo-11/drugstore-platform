package io.github.alexisTrejo11.drugstore.users.user.core.application.queries;

import javax.management.relation.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.alexisTrejo11.drugstore.users.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserStatus;

public record SearchUserQuery(
    UserStatus status,
    Role role,
    String emailLike,
    String phoneNumberLike,
    String firstNameLike,
    String lastNameLike,
    String joinedAtFrom,
    String joinedAtTo,
    String lastLoginAtFrom,
    String lastLoginAtTo,
    Pageable pageable) implements Query<Page<UserQueryResult>> {
  public SearchUserQuery {
    if (pageable == null) {
      pageable = Pageable.unpaged();
    }
  }

  public String toJson() {
    return String.format(
        "{\"status\":\"%s\", \"role\":\"%s\", \"emailLike\":\"%s\", \"phoneNumberLike\":\"%s\", \"firstNameLike\":\"%s\", \"lastNameLike\":\"%s\", \"joinedAtFrom\":\"%s\", \"joinedAtTo\":\"%s\", \"lastLoginAtFrom\":\"%s\", \"lastLoginAtTo\":\"%s\", \"pageInput\":%s}",
        status, role, emailLike, phoneNumberLike, firstNameLike, lastNameLike,
        joinedAtFrom, joinedAtTo, lastLoginAtFrom, lastLoginAtTo, pageable.toString());
  }
}
