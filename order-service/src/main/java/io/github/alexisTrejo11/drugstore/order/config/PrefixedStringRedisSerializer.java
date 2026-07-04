package io.github.alexisTrejo11.drugstore.order.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class PrefixedStringRedisSerializer implements RedisSerializer<String> {

  private final RedisProperties redisProperties;
  private final StringRedisSerializer delegate = StringRedisSerializer.UTF_8;

  public PrefixedStringRedisSerializer(RedisProperties redisProperties) {
    this.redisProperties = redisProperties;
  }

  @Override
  public byte[] serialize(String key) {
    if (key == null) {
      return null;
    }
    return delegate.serialize(redisProperties.prefixKey(key));
  }

  @Override
  public String deserialize(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    String key = delegate.deserialize(bytes);
    String prefix = redisProperties.normalizedPrefix();
    if (!prefix.isEmpty() && key.startsWith(prefix)) {
      return key.substring(prefix.length());
    }
    return key;
  }
}
