package at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress;

import at.backend.drugstore.microservice.common_classes.Models.Address.AddressType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AddressUpdateDTO {

    @JsonProperty("address_id")
    @NotNull(message = "Address ID cannot be null")
    Long addressId;

    @JsonProperty("street")
    @NotBlank(message = "Street cannot be blank")
    @Size(max = 255, message = "Street must not exceed 255 characters")
    private String street;

    @JsonProperty("house_number")
    @Min(value = 1, message = "House number must be greater than 0")
    private int houseNumber;

    @JsonProperty("neighborhood")
    @NotBlank(message = "Neighborhood cannot be blank")
    @Size(max = 255, message = "Neighborhood must not exceed 255 characters")
    private String neighborhood;

    @JsonProperty("state")
    @NotBlank(message = "State cannot be blank")
    @Size(max = 255, message = "State must not exceed 255 characters")
    private String state;

    @JsonProperty("country")
    @NotBlank(message = "Country cannot be blank")
    @Size(max = 255, message = "Country must not exceed 255 characters")
    private String country;

    @JsonProperty("description")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @JsonProperty("zip_code")
    @Pattern(regexp = "\\d{5}", message = "Zip code must be a 5-digit number")
    private String zipCode;

    @JsonProperty("inner_number")
    @Size(max = 50, message = "Inner number must not exceed 50 characters")
    private String innerNumber;

    @JsonProperty("address_type")
    @Pattern(regexp = "(HOUSE|DEPARTMENT)", message = "Address type must be either HOUSE or DEPARTMENT")
    private AddressType addressType;
}
