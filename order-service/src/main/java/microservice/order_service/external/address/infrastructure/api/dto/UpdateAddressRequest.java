package microservice.order_service.external.address.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import microservice.order_service.external.address.domain.model.BuildingType;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.hibernate.validator.constraints.Length;


@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateAddressRequest(
        @NotBlank(message = "Street is required")
        @Size(max = 120, message = "Street must be at most 120 characters")
        String street,

        @JsonProperty("inner_number")
        @NotBlank(message = "Inner number is required")
        @Length(min = 3, max = 10, message = "Outer number must be between 3 and 10 characters")
        String innerNumber,

        @JsonProperty("outer_number")
        @NotNull(message = "Outer number is required")
        @NotBlank(message = "Outer number is required")
        @Length(min = 3, max = 10, message = "Outer number must be between 3 and 10 characters")
        String outerNumber,

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


    public UpdateAddressRequest normalize() {
        return new UpdateAddressRequest(
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

    public DeliveryAddress toDomain(AddressID addressID, UserID userID) {
        var normalized = normalize();
        return DeliveryAddress.builder()
                .id(addressID)
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
                .userID(userID)
                .build();
    }

    public DeliveryAddress toDomain(AddressID addressID) {
        var normalized = normalize();
        return DeliveryAddress.builder()
                .id(addressID)
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
