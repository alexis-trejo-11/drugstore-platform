package microservice.order_service.external.address.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.util.Objects;


@Getter
@Builder
public class DeliveryAddress {
    private final AddressID id;
    private final String country;
    private final String state;
    private final String city;
    private final String neighborhood;
    private final String zipCode;
    private final String street;
    private final BuildingType buildingType;
    private final String innerNumber;
    private final String outerNumber;
    private final String additionalInfo;
    @Setter
    private boolean isDefault;
    private UserID userID;

    public void markAsDefault() {
        this.isDefault = true;
    }

    public void unsetDefault() {
        this.isDefault = false;
    }

    public void toggleDefault() {
        this.isDefault = !this.isDefault;
    }

    public static DeliveryAddress create(
            UserID userID, String country, String state, String city, String street,
            String innerNumber, String outerNumber, String neighborhood,
            BuildingType buildingType, String zipCode, String additionalInfo, boolean isDefault
    ) {
        return DeliveryAddress.builder()
                .id(AddressID.generate())
                .street(street)
                .isDefault(isDefault)
                .userID(userID)
                .country(country)
                .state(state)
                .city(city)
                .innerNumber(innerNumber)
                .outerNumber(outerNumber)
                .neighborhood(neighborhood)
                .buildingType(buildingType)
                .zipCode(zipCode)
                .additionalInfo(additionalInfo)
                .build();
    }


    public DeliveryAddress update(
            String country,
            String state,
            String city,
            String street,
            String innerNumber,
            String outerNumber,
            String neighborhood,
            BuildingType buildingType,
            String zipCode,
            String additionalInfo
    ) {
        return DeliveryAddress.builder().
                id(id)
                .userID(userID)
                .isDefault(isDefault)
                .country(country)
                .city(city != null ? city : this.city)
                .state(state != null ? state : this.state)
                .street(street != null ? street : this.street)
                .innerNumber(innerNumber != null ? innerNumber : this.innerNumber)
                .outerNumber(outerNumber != null ? outerNumber : this.outerNumber)
                .neighborhood(neighborhood != null ? neighborhood : this.neighborhood)
                .buildingType(buildingType != null ? buildingType : this.buildingType)
                .zipCode(zipCode != null ? zipCode : this.zipCode)
                .additionalInfo(additionalInfo != null ? additionalInfo : this.additionalInfo)
                .build();
    }

    public String getFullAddress() {
        return String.format("%s %s, %s, %s, %s %s",
                street,
                outerNumber != null ? outerNumber : "",
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