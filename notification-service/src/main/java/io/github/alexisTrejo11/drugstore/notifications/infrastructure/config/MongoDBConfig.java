package io.github.alexisTrejo11.drugstore.notifications.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration for the notification service
 */
@Configuration
@EnableMongoRepositories(basePackages = "io.github.alexisTrejo11.drugstore.notifications.infrastructure.persistence")
@EnableMongoAuditing
public class MongoDBConfig {

  // Additional MongoDB configuration can be added here if needed
  // For example: custom converters, transaction managers, etc.
}
