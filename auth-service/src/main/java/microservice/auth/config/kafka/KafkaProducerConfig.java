package microservice.auth.config.kafka;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class KafkaProducerConfig {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KafkaProducerConfig.class);

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${spring.kafka.producer.acks:all}")
  private String acks;

  @Value("${spring.kafka.producer.retries:3}")
  private Integer retries;

  @Value("${spring.kafka.producer.batch-size:16384}")
  private Integer batchSize;

  @Value("${spring.kafka.producer.linger-ms:10}")
  private Integer lingerMs;

  @Value("${spring.kafka.producer.buffer-memory:33554432}")
  private Long bufferMemory;

  @Value("${spring.kafka.producer.compression-type:snappy}")
  private String compressionType;

  @Value("${spring.kafka.producer.max-in-flight-requests:5}")
  private Integer maxInFlightRequests;

  @Value("${spring.kafka.producer.enable-idempotence:true}")
  private Boolean enableIdempotence;

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    return mapper;
  }

  @Bean
  public Map<String, Object> producerConfigs() {
    Map<String, Object> props = new HashMap<>();

    // Configuration of Kafka Producer
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    // Serializers (how to convert key and value to bytes)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    // Reliability
    props.put(ProducerConfig.ACKS_CONFIG, acks);
    props.put(ProducerConfig.RETRIES_CONFIG, retries);
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);

    // Performance and batching
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
    props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
    props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequests);

    // Timeouts
    props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000); // 30 seconds
    props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000); // 2 minutes

    // Metadata
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "auth-service-producer");

    log.info("Kafka Producer configured with bootstrap servers: {}", bootstrapServers);

    return props;
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
