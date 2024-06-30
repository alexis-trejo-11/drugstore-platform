package at.backend.drugstore.microservice.common_models.DTO.Client.Adress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressDTO {
    private Long id;

    private String street;

    @JsonProperty("house_number")
    private int houseNumber;

    @JsonProperty("neighborhood")
    private String neighborhood;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("description")
    private String description;

    @JsonProperty("zip_code")
    private int zipCode;

    @JsonProperty("inner_number")
    private String innerNumber;

    @JsonProperty("address_type")
    private String addressType;

    @JsonProperty("client_id")
    private Long clientId;
}
