package io.github.alexisTrejo11.drugstore.notifications.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.alexisTrejo11.drugstore.notifications.application.dto.NotificationMetrics;
import io.github.alexisTrejo11.drugstore.notifications.domain.model.Notification;

public interface NotificationMonitoringService {

  Page<Notification> getNotificationsByUserId(String userId, Pageable pageable);

  Page<Notification> getNotificationsByStatus(String status, Pageable pageable);

  Notification getNotificationById(String id);

  Notification getNotificationByCorrelationId(String correlationId);

  Notification getNotificationByEventId(String id);

  Page<Notification> getRecentNotification(Pageable pageable);

  NotificationMetrics getNotificationMetrics();

}
