package at.backend.drugstore.microservice.common_classes.DTOs.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestEmployeeUser {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("company_email")
    private String companyEmail;

    @JsonProperty("company_phone")
    private String companyPhone;

    @NotBlank(message = "password is empty")
    @Schema(description = "Password of the client", required = true, example = "strongPassword123")
    @JsonProperty("password")
    private String password;
}
