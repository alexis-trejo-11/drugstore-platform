package io.github.alexisTrejo11.drugstore.users.app.user.core.application.result;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.enums.UserStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteria {
    private String email;
    private String phoneNumber;
    private UserStatus status;
    private UserRole role;
    private String firstName;
    private String lastName;
    private LocalDate createdAfter;
    private LocalDate createdBefore;
}
