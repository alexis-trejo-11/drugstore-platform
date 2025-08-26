package user_service.modules.users.core.application.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;
import user_service.modules.users.core.domain.models.enums.Gender;

@Builder
public record ProfileUpdate(
                String firstName,
                String lastName,
                String phoneNumber,
                Gender gender,
                String bio,
                String avatarUrl,
                String coverUrl,
                LocalDate dateOfBirth,
                UUID userId) {

}
