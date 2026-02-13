package microservice.auth.app.auth.core.domain.valueobjects;

import java.util.UUID;

public record UserId(String value) {
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    public static UserId of(UUID value) {
        return new UserId(value.toString());
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

}
