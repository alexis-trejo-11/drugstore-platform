package io.github.alexisTrejo11.drugstore.users.config.redis;

import io.lettuce.core.RedisURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(name = "spring.data.redis.url")
public class RedisConnectionFactoryConfig {

  @Bean
  @Primary
  public RedisConnectionFactory redisConnectionFactory(
      @Value("${spring.data.redis.url}") String redisUrl) {
    RedisURI uri = RedisURI.create(redisUrl.trim());

    if (!StringUtils.hasText(uri.getHost())) {
      throw new IllegalArgumentException(
          "Invalid REDIS_URL / spring.data.redis.url: host is missing. "
              + "Use a full URI, e.g. redis://localhost:6379 or "
              + "rediss://default:password@endpoint.upstash.io:6379");
    }

    RedisStandaloneConfiguration standalone = new RedisStandaloneConfiguration();
    standalone.setHostName(uri.getHost());
    standalone.setPort(uri.getPort() > 0 ? uri.getPort() : RedisURI.DEFAULT_REDIS_PORT);

    if (StringUtils.hasText(uri.getUsername())) {
      standalone.setUsername(uri.getUsername());
    }
    if (uri.getPassword() != null && uri.getPassword().length > 0) {
      standalone.setPassword(RedisPassword.of(new String(uri.getPassword())));
    }

    LettuceClientConfiguration.LettuceClientConfigurationBuilder clientBuilder =
        LettuceClientConfiguration.builder();
    if (uri.isSsl()) {
      clientBuilder.useSsl();
    }

    return new LettuceConnectionFactory(standalone, clientBuilder.build());
  }
}
