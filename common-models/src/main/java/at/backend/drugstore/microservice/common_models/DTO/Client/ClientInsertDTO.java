package at.backend.drugstore.microservice.common_models.DTO.Client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ClientInsertDTO {

    @JsonProperty("first_name")
    @NotNull(message = "first_name is obligatory")
    @NotBlank(message = "first_name is obligatory")
    private String firstName;

    @JsonProperty("last_name")
    @NotNull(message = "last_name is obligatory")
    @NotBlank(message = "last_name is obligatory")
    private String lastName;

    @JsonProperty("birthdate")
    private LocalDate birthdate;

    @JsonProperty("phone")
    private String phone;
}