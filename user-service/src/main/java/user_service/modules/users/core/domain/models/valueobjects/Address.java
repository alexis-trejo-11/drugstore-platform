package user_service.modules.users.core.domain.models.valueobjects;

import java.util.Objects;

public record Address(
        String street,
        String city,
        String postalCode,
        String country) {
    public Address {
        Objects.requireNonNull(street);
        Objects.requireNonNull(city);
    }
}