package io.github.alexisTrejo11.drugstore.products.config.ratelimit;

import io.github.alexisTrejo11.drugstore.products.config.PrefixedStringRedisSerializer;
import io.github.alexisTrejo11.drugstore.products.config.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
@ConditionalOnProperty(prefix = "app.rate-limit.global", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RateLimiterConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory connectionFactory,
      RedisProperties redisProperties) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    PrefixedStringRedisSerializer keySerializer = new PrefixedStringRedisSerializer(redisProperties);
    template.setKeySerializer(keySerializer);
    template.setHashKeySerializer(keySerializer);
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
  }
}
