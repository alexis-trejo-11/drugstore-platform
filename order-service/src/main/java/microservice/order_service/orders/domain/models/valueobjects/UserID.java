package microservice.order_service.orders.domain.models.valueobjects;

import java.util.UUID;

public record UserID(String value) {
    public UserID {
        if (value == null) {
            throw new IllegalArgumentException("UserID cannot be null");
        }
    }

    public static UserID of(UUID value) {
        return new UserID(value.toString());
    }
    public static UserID of(String value) {
        return new UserID(value);
    }

}
