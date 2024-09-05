package at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
public class AddressInsertDTO {

    @NotNull(message = "street is obligatory")
    @NotBlank(message = "street can't be empty")
    private String street;

    @NotNull
    @Positive
    @JsonProperty("house_number")
    private int houseNumber;

    @NotNull(message = "neighborhood is obligatory")
    @NotBlank(message = "neighborhood can't be empty")
    @JsonProperty("neighborhood")
    private String neighborhood;

    @NotNull(message = "city is obligatory")
    @NotBlank(message = "city can't be empty")
    private String city;

    @NotNull(message = "city is obligatory")
    @NotBlank(message = "city can't be empty")
    @JsonProperty("state")
    private String state;

    @NotNull(message = "country is obligatory")
    @NotBlank(message = "country can't be empty")
    @JsonProperty("country")
    private String country;

    @JsonProperty("description")
    private String description;

    @NotNull(message = "zipCode is obligatory")
    @NotBlank(message = "zip_code can not be blank")
    @JsonProperty("zip_code")
    private String zipCode;

    @JsonProperty("inner_number")
    private String innerNumber;

    @JsonProperty("address_type")
    private String addressType;

}

