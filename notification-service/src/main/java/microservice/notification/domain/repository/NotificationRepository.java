package microservice.notification.domain.repository;

import microservice.notification.domain.model.Notification;
import microservice.notification.domain.valueobject.NotificationId;
import microservice.notification.domain.valueobject.NotificationStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Notification aggregate
 */
public interface NotificationRepository {

  Notification save(Notification notification);

  Optional<Notification> findById(NotificationId id);

  List<Notification> findByUserId(String userId);

  List<Notification> findByStatus(NotificationStatus status);

  List<Notification> findPendingNotifications();

  List<Notification> findFailedNotificationsForRetry(int maxRetries);

  void deleteById(NotificationId id);

  boolean existsById(NotificationId id);
}
