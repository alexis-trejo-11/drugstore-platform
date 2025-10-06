package microservice.order_service.external.address.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import microservice.order_service.external.address.domain.model.BuildingType;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;


@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DeliveryAddressRequest(
        @NotBlank(message = "Street is required")
        @Size(max = 120, message = "Street must be at most 120 characters")
        String street,

        @JsonProperty("inner_number")
        @Positive(message = "Inner number must be positive")
        @Max(value = 99999, message = "Inner number too large")
        Integer innerNumber,

        @JsonProperty("outer_number")
        @NotNull(message = "Outer number is required")
        @Positive(message = "Outer number must be positive")
        @Max(value = 99999, message = "Outer number too large")
        Integer outerNumber,

        @Size(max = 80, message = "Neighborhood must be at most 80 characters")
        String neighborhood,

        @JsonProperty("building_type")
        @NotNull(message = "Building type is required")
        BuildingType buildingType,

        @NotBlank(message = "City is required")
        @Size(max = 80, message = "City must be at most 80 characters")
        String city,

        @NotBlank(message = "State is required")
        @Size(max = 80, message = "State must be at most 80 characters")
        String state,

        @NotBlank(message = "Zip code is required")
        @Pattern(regexp = "[0-9A-Za-z\\- ]{4,10}", message = "Zip code format is invalid")
        @JsonProperty("zip_code")
        String zipCode,

        @NotBlank(message = "Country is required")
        @Size(max = 80, message = "Country must be at most 80 characters")
        String country,

        @JsonProperty("additional_info")
        @Size(max = 200, message = "Additional info must be at most 200 characters")
        String additionalInfo
) {


    public DeliveryAddressRequest normalize() {
        return new DeliveryAddressRequest(
                trimOrNull(street),
                innerNumber,
                outerNumber,
                trimOrNull(neighborhood),
                buildingType,
                trimOrNull(city),
                trimOrNull(state),
                zipCode != null ? zipCode.trim() : null,
                trimOrNull(country),
                trimOrNull(additionalInfo)
        );
    }

    private String trimOrNull(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }

    public DeliveryAddress toDomain() {
        var normalized = normalize();
        return DeliveryAddress.builder()
                .street(normalized.street)
                .innerNumber(normalized.innerNumber)
                .outerNumber(normalized.outerNumber)
                .neighborhood(normalized.neighborhood)
                .buildingType(normalized.buildingType)
                .city(normalized.city)
                .state(normalized.state)
                .zipCode(normalized.zipCode)
                .country(normalized.country)
                .additionalInfo(normalized.additionalInfo)
                .build();
    }

    public DeliveryAddress toDomainWithID(AddressID id) {
        var normalized = normalize();
        return DeliveryAddress.builder()
                .id(id)
                .street(normalized.street)
                .innerNumber(normalized.innerNumber)
                .outerNumber(normalized.outerNumber)
                .neighborhood(normalized.neighborhood)
                .buildingType(normalized.buildingType)
                .city(normalized.city)
                .state(normalized.state)
                .zipCode(normalized.zipCode)
                .country(normalized.country)
                .additionalInfo(normalized.additionalInfo)
                .build();
    }
}
