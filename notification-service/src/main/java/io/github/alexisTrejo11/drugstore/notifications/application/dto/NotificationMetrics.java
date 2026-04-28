package io.github.alexisTrejo11.drugstore.notifications.application.dto;

import java.time.LocalDateTime;

public record NotificationMetrics(
    long totalNotifications,
    long pendingNotifications,
    long sentNotifications,
    long failedNotifications,
    long retryableNotifications,
    LocalDateTime from,
    LocalDateTime to) {

}
