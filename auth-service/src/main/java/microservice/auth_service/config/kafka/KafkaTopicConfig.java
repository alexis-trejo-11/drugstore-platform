package microservice.auth_service.config.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {
  private final static Logger logger = LoggerFactory.getLogger(KafkaTopicConfig.class);

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

}
