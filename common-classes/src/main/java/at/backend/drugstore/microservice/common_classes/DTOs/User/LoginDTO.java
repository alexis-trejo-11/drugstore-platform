package at.backend.drugstore.microservice.common_classes.DTOs.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class LoginDTO {

    @Schema(description = "Email address of the user", example = "user@example.com")
    @JsonProperty("email")
    private String email;

    @Schema(description = "Phone number of the user", example = "+123456789")
    @JsonProperty("phone_number")
    private String phoneNumber;


    @NotBlank(message = "password is empty")
    @Schema(description = "Password of the user", required = true, example = "strongPassword123")
    @JsonProperty("password")
    private String password;
}
