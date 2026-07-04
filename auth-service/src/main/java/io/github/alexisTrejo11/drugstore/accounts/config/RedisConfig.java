package io.github.alexisTrejo11.drugstore.accounts.config;

import org.springframework.context.annotation.Bean;
import io.github.alexisTrejo11.drugstore.accounts.config.PrefixedStringRedisSerializer;
import io.github.alexisTrejo11.drugstore.accounts.config.RedisProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RedisConfig {

  /**
   * Configures RedisTemplate with proper JSON serialization for complex objects.
   * Uses Jackson for automatic serialization/deserialization of domain models.
   * 
   * @param connectionFactory the Redis connection factory
   * @return configured RedisTemplate
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory connectionFactory,
      RedisProperties redisProperties) {
    log.info("Configuring RedisTemplate with JSON serialization");

    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Configure ObjectMapper for proper Java 8 date/time serialization
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.activateDefaultTyping(
        objectMapper.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL);

    // Use String serializer for keys
    PrefixedStringRedisSerializer keySerializer = new PrefixedStringRedisSerializer(redisProperties);
    template.setKeySerializer(keySerializer);
    template.setHashKeySerializer(keySerializer);

    // Use JSON serializer for values
    GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
    template.setValueSerializer(jsonSerializer);
    template.setHashValueSerializer(jsonSerializer);

    template.setEnableTransactionSupport(true);
    template.afterPropertiesSet();

    log.info("RedisTemplate configured successfully");
    return template;
  }

  @Bean
  public StringRedisTemplate stringRedisTemplate(
      RedisConnectionFactory connectionFactory,
      RedisProperties redisProperties) {
    StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
    PrefixedStringRedisSerializer keySerializer = new PrefixedStringRedisSerializer(redisProperties);
    template.setKeySerializer(keySerializer);
    template.setHashKeySerializer(keySerializer);
    template.afterPropertiesSet();
    return template;
  }
}
