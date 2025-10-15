package microservice.order_service.external.address.domain.model;

import java.util.UUID;

public record AddressID(String value) {
    public AddressID {
        if (value == null) {
            throw new IllegalArgumentException("AddressID cannot be null");
        }
    }

    public static AddressID of(UUID value) {
        return new AddressID(value.toString());
    }
    public static AddressID of(String value) {
        return new AddressID(value);
    }
    public static AddressID generate() {
        return new AddressID(UUID.randomUUID().toString());
    }
}
