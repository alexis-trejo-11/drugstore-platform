package at.backend.drugstore.microservice.common_models.DTO.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ClientSignUpDTO {

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotNull(message = "password is obligatory")
    @NotBlank(message = "first is empty")
    @JsonProperty("password")
    private String password;

    @NotNull(message = "birthdate is obligatory")
    @JsonProperty("birthdate")
    private LocalDate birthdate;

    @JsonProperty("first_name")
    @NotNull(message = "first name is obligatory")
    @NotBlank(message = "first name can't be empty")
    private String firstName;

    @JsonProperty("last_name")
    @NotNull(message = "last name is obligatory")
    @NotBlank(message = "last name can't be empty")
    private String lastName;

}
