package io.github.alexisTrejo11.drugstore.carts.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import io.github.alexisTrejo11.drugstore.carts.product.core.domain.event.ProductEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${spring.kafka.consumer.group-id}")
  private String groupId;

  @Bean
  public ConsumerFactory<String, ProductEvent> consumerFactory() {
    Map<String, Object> props = new HashMap<>();

    // Basic consumer configuration
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

    // Configure the delegating deserializers
    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

    // JsonDeserializer configuration
    props.put(JsonDeserializer.TRUSTED_PACKAGES,
        "io.github.alexisTrejo11.drugstore.carts.product.core.domain.event");
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE,
        "io.github.alexisTrejo11.drugstore.carts.product.core.domain.event.ProductEvent");
    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

    // Consumer behavior
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
    props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);
    props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
    props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 45000);

    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ProductEvent> kafkaListenerContainerFactory(
      ConsumerFactory<String, ProductEvent> consumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, ProductEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(3); // Number of concurrent threads/consumers
    factory.getContainerProperties().setPollTimeout(3000);
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

    // Configure error handler with retry mechanism
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(
        (record, exception) -> {
          // This is called when retries are exhausted
          System.err.println("Retries exhausted for record: " + record.value() +
              " - Exception: " + exception.getMessage());
        },
        new FixedBackOff(1000L, 3) // 1 second delay, max 3 retries
    );

    // Add non-retryable exceptions (these will not be retried)
    errorHandler.addNotRetryableExceptions(
        IllegalArgumentException.class,
        NullPointerException.class);

    factory.setCommonErrorHandler(errorHandler);

    return factory;
  }

  // Optional: If you need to send events from cart-service
  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }
}