package at.backend.drugstore.microservice.common_classes.DTOs.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class ClientLoginDTO {

    @Schema(description = "Email address of the client", example = "client@example.com")
    @JsonProperty("email")
    private String email;

    @Schema(description = "Phone number of the client", example = "+123456789")
    @JsonProperty("phone_number")
    private String phoneNumber;


    @NotBlank(message = "password is empty")
    @Schema(description = "Password of the client", required = true, example = "strongPassword123")
    @JsonProperty("password")
    private String password;
}
