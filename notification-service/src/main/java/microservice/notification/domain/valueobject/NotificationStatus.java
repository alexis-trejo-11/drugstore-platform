package microservice.notification.domain.valueobject;

/**
 * Enum representing the status of a notification
 */
public enum NotificationStatus {
    PENDING("pending"),
    PROCESSING("processing"),
    SENT("sent"),
    DELIVERED("delivered"),
    FAILED("failed"),
    BOUNCED("bounced"),
    CANCELLED("cancelled");

    private final String value;

    NotificationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isFinal() {
        return this == SENT || this == DELIVERED || this == FAILED || this == BOUNCED || this == CANCELLED;
    }

    public boolean isSuccess() {
        return this == SENT || this == DELIVERED;
    }
}
