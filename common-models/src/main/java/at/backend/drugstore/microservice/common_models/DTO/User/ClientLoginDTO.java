package at.backend.drugstore.microservice.common_models.DTO.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ClientLoginDTO {

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotNull(message = "password is obligatory")
    @NotBlank(message = "password is empty")
    @JsonProperty("password")
    private String password;

}
