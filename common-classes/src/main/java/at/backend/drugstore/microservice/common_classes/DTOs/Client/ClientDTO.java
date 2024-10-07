package at.backend.drugstore.microservice.common_classes.DTOs.Client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
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

    @JsonProperty("phone_number")
    private String phone_number;

    @JsonProperty("email")
    private String email;

    @JsonProperty("is_client_premium")
    private boolean isClientPremium;

    @JsonProperty("loyalty_points")
    private int loyaltyPoints;


}
