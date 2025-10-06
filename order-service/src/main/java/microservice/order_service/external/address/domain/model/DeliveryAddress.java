package microservice.order_service.external.address.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;

import java.util.Objects;


@Getter
@Builder
public class DeliveryAddress {
    private final AddressID id;
    private final String street;
    private final Integer innerNumber;
    private final Integer outerNumber;
    private final String neighborhood;
    private final BuildingType buildingType;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String country;
    private final String additionalInfo;
    @Setter
    private boolean isDefault; // único campo mutable


    public void markAsDefault() { this.isDefault = true; }
    public void unsetDefault() { this.isDefault = false; }
    public void toggleDefault() { this.isDefault = !this.isDefault; }


    public DeliveryAddress withDefault(boolean value) {
        if (this.isDefault == value) return this;
        return new DeliveryAddress(id, street, innerNumber, outerNumber, neighborhood, buildingType, city, state,
                zipCode, country, additionalInfo, value);
    }

    public String getFullAddress() {
        return String.format("%s %s, %s, %s, %s %s",
                street,
                outerNumber != null ? outerNumber.toString() : "",
                city,
                state,
                country,
                zipCode
        ).replaceAll("\\s+", " ").trim();
    }

    public boolean isComplete() {
        return street != null && !street.trim().isEmpty() &&
                city != null && !city.trim().isEmpty() &&
                state != null && !state.trim().isEmpty() &&
                country != null && !country.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryAddress that = (DeliveryAddress) o;
        return Objects.equals(street, that.street) &&
                Objects.equals(innerNumber, that.innerNumber) &&
                Objects.equals(outerNumber, that.outerNumber) &&
                Objects.equals(city, that.city) &&
                Objects.equals(state, that.state) &&
                Objects.equals(zipCode, that.zipCode) &&
                Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, innerNumber, outerNumber, city, state, zipCode, country);
    }

}