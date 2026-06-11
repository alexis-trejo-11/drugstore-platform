---
# InfrastructureMetric[]
metrics:
  - label: "Java Version"
    value: "23"
    icon: "java"
    description: "Running on Eclipse Temurin JDK 23 with Spring Boot 3.3.2"

  - label: "Redis"
    value: "7-alpine"
    icon: "redis"
    description: "Session storage, rate limiting, and token persistence"

  - label: "Kafka"
    value: "PLACEHOLDER"
    icon: "kafka"
    description: "Event streaming platform for publishing auth events"

  - label: "gRPC"
    value: "1.60.0"
    icon: "grpc"
    description: "Protobuf-based RPC for user-service communication"

  - label: "Container Image"
    value: "eclipse-temurin:23-jre-alpine"
    icon: "docker"
    description: "Multi-stage Docker build with shared-kernel library"

  - label: "HTTPS Port"
    value: "8443"
    icon: "lock"
    description: "SSL/TLS enabled with keystore.p12 certificate (internal only — traffic enters via Nginx)"

  - label: "Reverse Proxy"
    value: "Nginx 1.27"
    icon: "nginx"
    description: "TLS termination on :443, HTTP→HTTPS redirect on :80, least_conn load balancing across replicas"

  - label: "Health Check"
    value: "/actuator/health"
    icon: "heart"
    description: "Spring Boot Actuator with 30s interval"

  - label: "Integration tests"
    value: "Testcontainers"
    icon: "test-tube"
    description: "JUnit + Redis & Kafka containers; in-process gRPC UserService; profiles integration-test + test; Docker required (skipped if unavailable)"

# CloudService[]
cloudServices:
  - name: "PLACEHOLDER: AWS ElastiCache"
    purpose: "Managed Redis for production session and rate limit storage"
    icon: "aws-elasticache"
    cost: "PLACEHOLDER: ~$20-80/month depending on node type"

  - name: "PLACEHOLDER: AWS MSK (Managed Streaming for Kafka)"
    purpose: "Managed Kafka for production event streaming"
    icon: "aws-msk"
    cost: "PLACEHOLDER: ~$100-500/month depending on brokers"

  - name: "PLACEHOLDER: AWS ECS/EKS"
    purpose: "Container orchestration for running the auth-service"
    icon: "aws-ecs"
    cost: "PLACEHOLDER: ~$30-150/month depending on Fargate/EC2"

  - name: "PLACEHOLDER: AWS Certificate Manager"
    purpose: "SSL/TLS certificates management for HTTPS"
    icon: "aws-certificate"
    cost: "PLACEHOLDER: Free (with ELB)"

  - name: "PLACEHOLDER: AWS CloudWatch"
    purpose: "Logs and metrics collection for monitoring"
    icon: "aws-cloudwatch"
    cost: "PLACEHOLDER: ~$10-50/month based on log volume"

# DeploymentLayer[]
deploymentLayers:
  - name: "Client Layer"
    color: "#4CAF50"
    components:
      - name: "Frontend Application"
        icon: "react"
        description: "React/Angular/Vue frontend consuming auth API"
      - name: "Mobile App (Future)"
        icon: "mobile"
        description: "PLACEHOLDER: Mobile applications via HTTPS/REST"

  - name: "Reverse Proxy / Load Balancer Layer"
    color: "#009688"
    components:
      - name: "Nginx 1.27"
        icon: "nginx"
        description: "Terminates TLS on :443, redirects HTTP :80 to HTTPS, least_conn load-balances to auth-service replicas via Docker DNS"

  - name: "Application Layer"
    color: "#2196F3"
    components:
      - name: "Auth Service (Spring Boot)"
        icon: "spring"
        description: "Java 23 + Spring Boot 3.3.2 with DDD architecture"
      - name: "JWT Token Manager"
        icon: "token"
        description: "TokenFactory + TokenManager for ACCESS/REFRESH/ACTIVATION/TWO_FA"
      - name: "OAuth2 Authentication"
        icon: "oauth2"
        description: "CustomOAuth2UserService for social login"

  - name: "Communication Layer"
    color: "#FF9800"
    components:
      - name: "gRPC to User-Service"
        icon: "grpc"
        description: "Protobuf 3.25.1 + gRPC 1.60.0 for user data"
      - name: "Kafka Producer"
        icon: "kafka"
        description: "Spring Kafka for publishing 8+ event types"

  - name: "Storage Layer"
    color: "#9C27B0"
    components:
      - name: "Redis 7"
        icon: "redis"
        description: "Sessions (refresh tokens), rate limits, non-JWT tokens"
      - name: "User Service DB (PostgreSQL)"
        icon: "postgres"
        description: "External user data via gRPC (not direct DB access)"

# DockerFile[]
dockerFiles:
  - service: "auth-service"
    description: "Multi-stage Docker build with shared-kernel library compilation and gRPC support"
    content: |
      # Dockerfile (in the root of auth-service)
      FROM eclipse-temurin:23-jdk-alpine AS builder

      WORKDIR /app

      # Copy gradle wrapper and parent build files
      COPY gradlew .
      COPY gradle gradle
      COPY gradle.properties .
      COPY build.gradle .
      COPY settings.gradle .

      # Remove the org.gradle.java.home property as it points to SDKMAN path
      RUN sed -i '/org.gradle.java.home/d' gradle.properties

      # Copy shared-kernel library
      COPY libs/shared-kernel libs/shared-kernel

      # Copy auth-service build file
      COPY auth-service/build.gradle auth-service/

      # Give execution permissions to gradlew
      RUN chmod +x gradlew

      # Build shared-kernel and publish to local Maven
      RUN ./gradlew :libs:shared-kernel:publishToMavenLocal --no-daemon

      # Download auth-service dependencies
      RUN ./gradlew :auth-service:dependencies --no-daemon

      # Copy auth-service source code
      COPY auth-service/src auth-service/src

      # Build the application
      RUN ./gradlew :auth-service:bootJar --no-daemon

      # Execution stage
      FROM eclipse-temurin:23-jre-alpine

      WORKDIR /app

      # Create non-root user for security
      RUN addgroup -S spring && adduser -S spring -G spring

      # Copy the built JAR
      COPY --from=builder /app/auth-service/build/libs/*.jar app.jar

      # Copy the SSL keystore
      COPY auth-service/src/main/resources/keystore.p12 /app/keystore.p12

      # Create directory for logs and config
      RUN mkdir -p /app/logs /app/config

      # Give permissions to the user
      RUN chown -R spring:spring /app

      # Create entrypoint script
      RUN echo '#!/bin/sh' > /app/entrypoint.sh && \
          echo 'chown -R spring:spring /app/logs 2>/dev/null || true' >> /app/entrypoint.sh && \
          echo 'exec su-exec spring:spring java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} -Dlogging.file.path=/app/logs -jar /app/app.jar "$@"' >> /app/entrypoint.sh && \
          chmod +x /app/entrypoint.sh

      # Install runtime utilities
      USER root
      RUN apk add --no-cache su-exec wget

      # Health check (using HTTPS on 8443)
      HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
        CMD wget --quiet --tries=1 --spider --no-check-certificate https://localhost:8443/actuator/health || exit 1

      # Expose port (HTTPS)
      EXPOSE 8443

      # Entry command
      ENTRYPOINT ["/app/entrypoint.sh"]

  - service: "redis"
    description: "Redis 7 Alpine image for session storage and rate limiting"
    content: |
      image: redis:7-alpine
      container_name: auth-redis
      restart: unless-stopped
      command: redis-server --appendonly yes
      ports:
        - "6378:6379"
      volumes:
        - redis-data:/data
      networks:
        - drugstore-network
      healthcheck:
        test: ["CMD", "redis-cli", "ping"]
        interval: 10s
        timeout: 5s
        retries: 5

  - service: "nginx"
    description: "Nginx reverse proxy — terminates TLS on :443, redirects :80 to HTTPS, least_conn load-balances across auth-service replicas via Docker DNS. auth-service port 8443 is NOT exposed to the host."
    content: |
      image: nginx:1.27-alpine
      container_name: auth-nginx
      ports:
        - "80:80"    # HTTP → redirects to HTTPS
        - "443:443"  # HTTPS — TLS termination + load balancing
      volumes:
        - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
        - ./nginx/ssl/nginx.crt:/etc/nginx/ssl/nginx.crt:ro
        - ./nginx/ssl/nginx.key:/etc/nginx/ssl/nginx.key:ro
      depends_on:
        auth-service:
          condition: service_healthy
      networks:
        - drugstore-network
      healthcheck:
        test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost/health"]
        interval: 15s
        timeout: 5s
        retries: 3
---
# Infrastructure

> Docker Compose has Redis, Nginx reverse proxy, Prometheus, Loki, and Grafana. Nginx handles TLS termination on port 443 and HTTP-to-HTTPS redirection on port 80, load-balancing requests across auth-service replicas via Docker DNS (`least_conn`). The auth-service port 8443 is only exposed internally — all external traffic goes through Nginx. **Automated integration tests** pull Redis and Kafka images via Testcontainers and start an in-process gRPC UserService—Docker must be available to run them (otherwise skipped). For a full manual stack, still add Kafka and a real user-service if you need end-to-end beyond tests. Production: MSK or self-managed Kafka, managed TLS (e.g. AWS ACM), gRPC health checks, Kubernetes manifests. Note: auth-service has no local PostgreSQL — user persistence is via user-service (gRPC).
