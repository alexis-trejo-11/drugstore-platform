package microservice.notification.domain.valueobject;

/**
 * Enum representing the priority level of a notification
 */
public enum NotificationPriority {
    LOW(1),
    NORMAL(2),
    HIGH(3),
    URGENT(4);

    private final int level;

    NotificationPriority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isHigherThan(NotificationPriority other) {
        return this.level > other.level;
    }
}
