package at.backend.drugstore.microservice.common_models.DTOs.Client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ClientDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @NotNull(message = "birthdate is obligatory")
    @JsonProperty("birthdate")
    private LocalDate birthdate;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("is_client_premium")
    private boolean isClientPremium;

    @JsonProperty("loyalty_points")
    private int loyaltyPoints;


}
