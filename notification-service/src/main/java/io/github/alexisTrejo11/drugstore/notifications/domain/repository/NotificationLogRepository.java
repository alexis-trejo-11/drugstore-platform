package io.github.alexisTrejo11.drugstore.notifications.domain.repository;

import io.github.alexisTrejo11.drugstore.notifications.domain.model.NotificationLog;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for NotificationLog entity
 */
public interface NotificationLogRepository {

  NotificationLog save(NotificationLog log);

  Optional<NotificationLog> findById(String id);

  List<NotificationLog> findByNotificationId(NotificationId notificationId);

  List<NotificationLog> findRecentLogs(int limit);

  void deleteById(String id);
}
