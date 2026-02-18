package user_service.modules.users.core.application.result;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.modules.users.core.domain.models.enums.UserStatus;

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
