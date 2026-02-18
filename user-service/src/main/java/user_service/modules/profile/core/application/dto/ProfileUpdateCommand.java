package user_service.modules.profile.core.application.dto;

import java.time.LocalDate;

import lombok.Builder;
import user_service.modules.users.core.domain.models.enums.Gender;
import user_service.modules.users.core.domain.models.valueobjects.FullName;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

@Builder
public record ProfileUpdateCommand(
    FullName fullName,
    LocalDate dateOfBirth,
    PhoneNumber phoneNumber,
    String bio,
    String avatarUrl,
    String coverUrl,
    Gender gender,
    UserId userId) {

}
