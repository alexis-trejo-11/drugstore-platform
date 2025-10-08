package microservice.order_service.external.address.infrastructure.persistence.Model;

import jakarta.persistence.*;
import lombok.*;
import microservice.order_service.external.address.domain.model.BuildingType;

import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;

import java.util.Objects;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAddressModel {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    public UserModel user;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "zip_code", nullable = false, length = 10)
    private String zipCode;

    @Column(name = "neighborhood", length = 100)
    private String neighborhood;

    @Column(name = "street", nullable = false, length = 255)
    private String street;

    @Column(name = "building_type", length = 50)
    private String buildingType;

    @Column(name = "inner_number")
    private Integer innerNumber;

    @Column(name = "outer_number", nullable = false)
    private Integer outerNumber;

    @Column(name = "additional_info", length = 500)
    private String additionalInfo;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    // Métodos de utilidad
    public String getFullAddress() {
        return String.format("%s %d%s, %s, %s, %s %s",
                street,
                outerNumber,
                innerNumber != null ? " Int. " + innerNumber : "",
                city,
                state,
                country,
                zipCode
        );
    }

    public boolean isComplete() {
        return street != null && !street.trim().isEmpty() &&
                city != null && !city.trim().isEmpty() &&
                state != null && !state.trim().isEmpty() &&
                country != null && !country.trim().isEmpty() &&
                zipCode != null && !zipCode.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryAddressModel that = (DeliveryAddressModel) o;
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