package microservice.order_service.external.address.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import microservice.order_service.external.address.domain.model.BuildingType;
import microservice.order_service.external.address.domain.model.DeliveryAddress;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DeliveryAddressResponse(
        String id,

        String street,

        @JsonProperty("inner_number")
        String innerNumber,

        @JsonProperty("outer_number")
        String outerNumber,

        String neighborhood,

        @JsonProperty("building_type")
        BuildingType buildingType,

        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Zip code is required")
        @JsonProperty("zip_code")
        String zipCode,

        @NotBlank(message = "Country is required")
        String country,

        @JsonProperty("additional_info")
        String additionalInfo,

        @JsonProperty("is_default")
        Boolean isDefault
) {

    @JsonProperty("full_address")
    public String getFullAddress() {
        return String.format("%s %s%s, %s, %s, %s %s",
                street,
                outerNumber,
                innerNumber != null ? " Int. " + innerNumber : "",
                city,
                state,
                country,
                zipCode
        );
    }

    @JsonProperty("is_complete")
    public boolean isComplete() {
        return street != null && !street.trim().isEmpty() &&
                city != null && !city.trim().isEmpty() &&
                state != null && !state.trim().isEmpty() &&
                country != null && !country.trim().isEmpty() &&
                zipCode != null && !zipCode.trim().isEmpty();
    }

    public static DeliveryAddressResponse from(DeliveryAddress address) {
        if (address == null) return null;

        return DeliveryAddressResponse.builder()
                .street(address.getStreet())
                .innerNumber(address.getInnerNumber())
                .outerNumber(address.getOuterNumber())
                .neighborhood(address.getNeighborhood())
                .buildingType(address.getBuildingType())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .country(address.getCountry())
                .additionalInfo(address.getAdditionalInfo())
                .build();
    }
}
