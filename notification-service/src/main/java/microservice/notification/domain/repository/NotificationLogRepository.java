package microservice.notification.domain.repository;

import microservice.notification.domain.model.NotificationLog;
import microservice.notification.domain.valueobject.NotificationId;

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
