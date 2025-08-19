package microservice.auth.app.core.valueobjects;

import java.util.UUID;

public record SessionId(UUID value) {
    public SessionId {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    public static SessionId of(UUID value) {
        return new SessionId(value);
    }

    public static SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
