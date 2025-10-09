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

    @Column(name = "user_id", nullable = false, length = 36, insertable = false, updatable = false)
    private String userId;


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
    private String innerNumber;

    @Column(name = "outer_number", nullable = false)
    private String outerNumber;

    @Column(name = "additional_info", length = 500)
    private String additionalInfo;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

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

    public DeliveryAddressModel(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, innerNumber, outerNumber, city, state, zipCode, country);
    }
}