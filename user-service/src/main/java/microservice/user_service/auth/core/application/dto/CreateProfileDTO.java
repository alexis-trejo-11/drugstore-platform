package microservice.user_service.auth.core.application.dto;

import lombok.Builder;
import microservice.user_service.users.core.domain.models.enums.Gender;

import java.util.Date;

@Builder
public record CreateProfileDTO(
    String firstName,
    String lastName,
    Gender gender,
    Date birthDate,
    String bio,
    String profilePictureUrl,
    String coverPictureUrl
) {

}
