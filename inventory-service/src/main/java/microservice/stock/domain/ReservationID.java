package microservice.stock.domain;

import java.util.UUID;

public record ReservationID(String value) {
    public ReservationID {
        if (value == null) value = "";
        value = value.trim();
    }

    public static ReservationID of(String value) {
        return new ReservationID(value);
    }
    public static ReservationID of(UUID uuid) {
        return new ReservationID(uuid.toString());
    }
    public static ReservationID generate() {
        return new ReservationID(UUID.randomUUID().toString());
    }

}