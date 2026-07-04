package io.github.alexisTrejo11.drugstore.address.config.ratelimit;

import io.github.alexisTrejo11.drugstore.address.config.PrefixedStringRedisSerializer;
import io.github.alexisTrejo11.drugstore.address.config.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
public class RateLimiterConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory connectionFactory,
      RedisProperties redisProperties) {
    PrefixedStringRedisSerializer keySerializer = new PrefixedStringRedisSerializer(redisProperties);

    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(keySerializer);
    template.setHashKeySerializer(keySerializer);
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
  }
}
