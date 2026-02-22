package io.github.alexisTrejo11.drugstore.notifications.domain.repository;

import java.util.List;
import java.util.Optional;

import io.github.alexisTrejo11.drugstore.notifications.domain.model.NotificationTemplate;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationChannel;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationType;

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
