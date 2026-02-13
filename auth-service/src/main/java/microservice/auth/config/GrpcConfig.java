package microservice.auth.config;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

@Configuration
public class GrpcConfig {
  private static final Logger log = LoggerFactory.getLogger(GrpcConfig.class);

  @Value("${grpc.client.user-service.host:localhost}")
  private String userServiceHost;

  @Value("${grpc.client.user-service.port:9090}")
  private int userServicePort;

  @Value("${grpc.client.user-service.keep-alive-time:30}")
  private long keepAliveTime;

  @Value("${grpc.client.user-service.keep-alive-timeout:5}")
  private long keepAliveTimeout;

  @Value("${grpc.client.user-service.idle-timeout:60}")
  private long idleTimeout;

  @Value("${grpc.client.user-service.max-inbound-message-size:4194304}") // 4MB default
  private int maxInboundMessageSize;

  @Bean(destroyMethod = "shutdown")
  public ManagedChannel userServiceChannel() {
    log.info("Initializing gRPC channel to User Service at {}:{}",
        userServiceHost, userServicePort);

    return NettyChannelBuilder
        .forAddress(userServiceHost, userServicePort)
        .usePlaintext() // TODO: Switch to TLS in production
        .keepAliveTime(keepAliveTime, TimeUnit.SECONDS)
        .keepAliveTimeout(keepAliveTimeout, TimeUnit.SECONDS)
        .idleTimeout(idleTimeout, TimeUnit.SECONDS)
        .maxInboundMessageSize(maxInboundMessageSize)
        // Configuración de retry automático
        .enableRetry()
        .maxRetryAttempts(3)
        .build();
  }
}
