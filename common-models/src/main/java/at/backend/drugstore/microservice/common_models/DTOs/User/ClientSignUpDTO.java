package at.backend.drugstore.microservice.common_models.DTOs.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ClientSignUpDTO {

    @NotNull(message = "email is obligatory")
    @NotBlank(message = "email is empty")
    @Schema(description = "Email address of the client", required = true, example = "client@example.com")
    @JsonProperty("email")
    private String email;

    @NotNull(message = "phone number is obligatory")
    @NotBlank(message = "phone number is empty")
    @Schema(description = "Phone number of the client", required = true, example = "+123456789")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotNull(message = "password is obligatory")
    @NotBlank(message = "password is empty")
    @Schema(description = "Password of the client", required = true, example = "strongPassword123")
    @JsonProperty("password")
    private String password;

    @NotNull(message = "birthdate is obligatory")
    @Schema(description = "Birthdate of the client", required = true, example = "1990-01-01")
    @JsonProperty("birthdate")
    private LocalDate birthdate;

    @NotNull(message = "first name is obligatory")
    @NotBlank(message = "first name is empty")
    @Schema(description = "First name of the client", required = true, example = "John")
    @JsonProperty("first_name")
    private String firstName;

    @NotNull(message = "last name is obligatory")
    @NotBlank(message = "last name is empty")
    @Schema(description = "Last name of the client", required = true, example = "Doe")
    @JsonProperty("last_name")
    private String lastName;
}
