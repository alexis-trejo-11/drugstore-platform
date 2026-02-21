package microservice.notification.domain.repository;

import microservice.notification.domain.model.NotificationTemplate;
import microservice.notification.domain.valueobject.NotificationChannel;
import microservice.notification.domain.valueobject.NotificationType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for NotificationTemplate entity
 */
public interface NotificationTemplateRepository {

  NotificationTemplate save(NotificationTemplate template);

  Optional<NotificationTemplate> findById(String id);

  Optional<NotificationTemplate> findByTypeAndChannelAndLanguage(
      NotificationType type,
      NotificationChannel channel,
      String language);

  List<NotificationTemplate> findByType(NotificationType type);

  List<NotificationTemplate> findActiveTemplates();

  void deleteById(String id);

  boolean existsByTypeAndChannelAndLanguage(
      NotificationType type,
      NotificationChannel channel,
      String language);
}
