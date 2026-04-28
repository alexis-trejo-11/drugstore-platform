package io.github.alexisTrejo11.drugstore.accounts.config.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  // Topics for user service
  @Value("${kafka.topics.user.created:user.created}")
  private String userCreatedTopic;

  @Value("${kafka.topics.user.updated:user.updated}")
  private String userUpdatedTopic;

  @Value("${kafka.topics.user.deleted:user.deleted}")
  private String userDeletedTopic;

  // Topics for notification service
  @Value("${kafka.topics.notification.email-verification:notification.email-verification}")
  private String emailVerificationTopic;

  @Value("${kafka.topics.notification.two-factor-code:notification.two-factor-code}")
  private String twoFactorCodeTopic;

  @Value("${kafka.topics.notification.welcome-email:notification.welcome-email}")
  private String welcomeEmailTopic;

  // Topics for auth events
  @Value("${kafka.topics.auth.user-registered:auth.user-registered}")
  private String userRegisteredTopic;

  @Value("${kafka.topics.auth.user-login:auth.user-login}")
  private String userLoginTopic;

  @Value("${kafka.topics.auth.password-changed:auth.password-changed}")
  private String passwordChangedTopic;

  @Value("${kafka.topics.auth.account-activated:auth.account-activated}")
  private String accountActivatedTopic;

  @Value("${kafka.topics.auth.two-factor-enabled:auth.two-factor-enabled}")
  private String twoFactorEnabledTopic;

  @Value("${kafka.topics.auth.two-factor-disabled:auth.two-factor-disabled}")
  private String twoFactorDisabledTopic;

  // Configuration for partitions and replication factor
  @Value("${kafka.topics.partitions:3}")
  private Integer partitions;

  @Value("${kafka.topics.replication-factor:1}")
  private Short replicationFactor;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    return new KafkaAdmin(configs);
  }

  @Bean
  public NewTopic userCreatedTopic() {
    return TopicBuilder.name(userCreatedTopic)
        .partitions(partitions)
        .replicas(replicationFactor)
        .config("retention.ms", "604800000") // 7 days
        .config("compression.type", "snappy")
        .config("min.insync.replicas", "1") // Mín of 1 replica must acknowledge
        .build();
  }

  @Bean
  public NewTopic userUpdatedTopic() {
    return TopicBuilder.name(userUpdatedTopic)
        .partitions(partitions)
        .replicas(replicationFactor)
        .config("retention.ms", "604800000") // 7 días
        .config("compression.type", "snappy")
        .build();
  }

  @Bean
  public NewTopic userDeletedTopic() {
    return TopicBuilder.name(userDeletedTopic)
        .partitions(partitions)
        .replicas(replicationFactor)
        .config("retention.ms", "2592000000") // 30 días (mantener más tiempo)
        .config("compression.type", "snappy")
        .build();
  }

  @Bean
  public NewTopic emailVerificationTopic() {
    return TopicBuilder.name(emailVerificationTopic)
        .partitions(partitions)
        .replicas(replicationFactor)
        .config("retention.ms", "86400000") // 24 horas (notificaciones son más efímeras)
        .config("compression.type", "snappy")
        .build();
  }

  @Bean
  public NewTopic twoFactorCodeTopic() {
    return TopicBuilder.name(twoFactorCodeTopic)
        .partitions(partitions)
        .replicas(replicationFactor)
        .config("retention.ms", "86400000") // 24 horas
        .config("compression.type", "snappy")
        .build();
  }

  @Bean
  public NewTopic welcomeEmailTopic() {
    return TopicBuilder.name(welcomeEmailTopic)
        .partitions(partitions)
        .replicas(replicationFactor)
        .config("retention.ms", "86400000") // 24 horas
        .config("compression.type", "snappy")
        .build();
  }

  // Auth event topics
  @Bean
  public NewTopic userRegisteredTopic() {
    return TopicBuilder.name(userRegisteredTopic)
        .partitions(partitions)
        .replicas(replicationFactor)
        .config("retention.ms", "604800000") // 7 días
        .config("compression.type", "snappy")
        .config("min.insync.replicas", "1")
        .build();
  }

  @Bean
  public NewTopic userLoginTopic() {
    return TopicBuilder.name(userLoginTopic)
        .partitions(partitions)
        .replicas(replicationFactor)
        .config("retention.ms", "259200000") // 3 días (eventos de login menos críticos)
        .config("compression.type", "snappy")
        .build();
  }

  @Bean
  public NewTopic passwordChangedTopic() {
    return TopicBuilder.name(passwordChangedTopic)
        .partitions(partitions)
        .replicas(replicationFactor)
        .config("retention.ms", "2592000000") // 30 días (auditoría importante)
        .config("compression.type", "snappy")
        .config("min.insync.replicas", "1")
        .build();
  }

  @Bean
  public NewTopic accountActivatedTopic() {
    return TopicBuilder.name(accountActivatedTopic)
        .partitions(partitions)
        .replicas(replicationFactor)
        .config("retention.ms", "604800000") // 7 días
        .config("compression.type", "snappy")
        .build();
  }

  @Bean
  public NewTopic twoFactorEnabledTopic() {
    return TopicBuilder.name(twoFactorEnabledTopic)
        .partitions(partitions)
        .replicas(replicationFactor)
        .config("retention.ms", "2592000000") // 30 días (seguridad importante)
        .config("compression.type", "snappy")
        .config("min.insync.replicas", "1")
        .build();
  }

  @Bean
  public NewTopic twoFactorDisabledTopic() {
    return TopicBuilder.name(twoFactorDisabledTopic)
        .partitions(partitions)
        .replicas(replicationFactor)
        .config("retention.ms", "2592000000") // 30 días (seguridad importante)
        .config("compression.type", "snappy")
        .config("min.insync.replicas", "1")
        .build();
  }

}
