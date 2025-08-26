package user_service.modules.profile.infrastructure.presentation.rest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.modules.users.core.application.dto.ProfileUpdate;
import user_service.modules.users.core.domain.models.enums.Gender;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateProfileRequest {
    @NotEmpty(message = "First name cannot be empty")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    private Gender gender;

    private String bio;
    private String avatarUrl;
    private String coverUrl;

    @Past(message = "Date of birth must be in the past")
    private LocalDateTime dateOfBirth;

    public ProfileUpdate toProfileUpdate(UUID userId) {
        return ProfileUpdate.builder()
                .userId(userId)
                .avatarUrl(avatarUrl != null ? avatarUrl : null)
                .bio(bio != null ? bio : null)
                .coverUrl(coverUrl != null ? coverUrl : null)
                .dateOfBirth(dateOfBirth != null ? dateOfBirth.toLocalDate() : null)
                .firstName(firstName != null ? firstName : null)
                .lastName(lastName != null ? lastName : null)
                .gender(gender != null ? gender : null)
                .build();
    }
}
