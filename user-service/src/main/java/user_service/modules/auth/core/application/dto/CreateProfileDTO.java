package user_service.modules.auth.core.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.modules.users.core.domain.models.enums.Gender;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateProfileDTO {
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 3, max = 100, message = "First name must be between 1 and 100 characters")
        private String firstName;

        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 3, max = 100, message = "Last name must be between 1 and 100 characters")
        private String lastName;

        @NotNull(message = "Birth date cannot be null")
        @Past(message = "Birth date must be in the past")
        private LocalDate birthDate;

        @NotNull(message = "Gender cannot be null")
        private Gender gender;

}
