package microservice.users.core.models.valueobjects;

import java.util.Objects;

public record Address(
        String street,
        String city,
        String postalCode,
        String country
) {
    public Address {
        Objects.requireNonNull(street);
        Objects.requireNonNull(city);
    }
}