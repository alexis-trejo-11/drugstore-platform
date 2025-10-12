package microservice.order_service.orders.domain.models.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UserID(@JsonProperty("value") String value) {

    public UserID {
        if (value == null) {
            throw new IllegalArgumentException("UserID cannot be null");
        }
    }

    @JsonCreator
    public static UserID of(@JsonProperty("value") String value) {
        return new UserID(value);
    }

    public static UserID generate() {
        return new UserID(UUID.randomUUID().toString());
    }

    public static UserID of(UUID value) {
        return new UserID(value.toString());
    }

    public static UserID generateRandom() {
        return new UserID(UUID.randomUUID().toString());
    }
}
