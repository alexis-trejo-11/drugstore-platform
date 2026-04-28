package io.github.alexisTrejo11.drugstore.users.config.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
	public static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KafkaConsumerConfig.class);

	@Value("${spring.kafka.bootstrap-servers:localhost:9092}")
	private String bootstrapServers;

	@Value("${spring.kafka.consumer.group-id:user-service-group}")
	private String groupId;

	@Value("${spring.kafka.consumer.auto-offset-reset:earliest}")
	private String autoOffsetReset;

	@Value("${spring.kafka.consumer.enable-auto-commit:false}")
	private Boolean enableAutoCommit;

	@Value("${spring.kafka.consumer.max-poll-records:10}")
	private Integer maxPollRecords;

	@Value("${spring.kafka.consumer.fetch-min-bytes:1}")
	private Integer fetchMinBytes;

	@Value("${spring.kafka.consumer.fetch-max-wait-ms:500}")
	private Integer fetchMaxWaitMs;

	@Value("${spring.kafka.consumer.session-timeout-ms:30000}")
	private Integer sessionTimeoutMs;

	@Value("${spring.kafka.consumer.heartbeat-interval-ms:3000}")
	private Integer heartbeatIntervalMs;

	@Value("${spring.kafka.consumer.concurrency:3}")
	private Integer concurrency;


	@Bean
	public ObjectMapper kafkaObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();

		// Connection Config
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

		// Deserializers
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);


		// Json Deserializer Config
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
		props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Object.class);

		// Offset management
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);

		// Performance tuning
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
		props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes);
		props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWaitMs);

		// Session management
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
		props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatIntervalMs);

		// Others
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "user-service-consumer");
		props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // 5 minutos

		log.info("Kafka Consumer configured with group-id: {} and bootstrap servers: {}",
				groupId, bootstrapServers);

		return props;
	}


	@Bean
	public ConsumerFactory<String, Object> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs());
	}


	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
			KafkaErrorHandler kafkaErrorHandler) {

		ConcurrentKafkaListenerContainerFactory<String, Object> factory =
				new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConsumerFactory(consumerFactory());

		// Concurrencia: número de threads para procesar mensajes
		factory.setConcurrency(concurrency);

		// Modo de confirmación manual para mayor control
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

		// Error handler personalizado
		factory.setCommonErrorHandler(kafkaErrorHandler);

		// Auto startup
		factory.setAutoStartup(true);

		log.info("KafkaListenerContainerFactory configured with concurrency: {}", concurrency);

		return factory;
	}
}
