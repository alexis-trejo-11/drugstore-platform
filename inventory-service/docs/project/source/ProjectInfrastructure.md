---
# InfrastructureMetric[]
metrics:
- label: "API Response Time" value: "PLACEHOLDER" icon: "speed" description: "Average API response time for inventory operations"
- label: "Cache Hit Rate" value: "PLACEHOLDER" icon: "storage" description: "Redis cache hit rate for inventory queries"
- label: "Batch Expiration Alerts" value: "PLACEHOLDER" icon: "warning" description: "Number of batches near expiration (30 days threshold)"
- label: "Active Reservations" value: "PLACEHOLDER" icon: "lock" description: "Currently active stock reservations"
- label: "Reverse Proxy" value: "Nginx 1.27" icon: "nginx" description: "TLS :443, redirect :80; upstream inventory_backend → inventory-service:8080 (least_conn)"

# CloudService[]
cloudServices:
- name: "PostgreSQL" purpose: "Primary database for inventory, batches, reservations, and movements" icon: "postgresql" cost: "PLACEHOLDER"
- name: "Redis" purpose: "Caching layer for inventory queries and rate limiting" icon: "redis" cost: "PLACEHOLDER"
- name: "RabbitMQ" purpose: "Message queue for asynchronous inventory events" icon: "rabbitmq" cost: "PLACEHOLDER"

# DeploymentLayer[]
deploymentLayers:
- name: "Reverse Proxy / Edge"
  color: "#009688"
  components:
  - name: "Nginx 1.27"
    icon: "nginx"
    description: "inventory-nginx — terminates TLS on host :443"

- name: "Application Layer"
  color: "#4CAF50"
  components:
  - name: "Inventory Service" icon: "spring" description: "Spring Boot 3.3.2 application with Java 23 (build.gradle) but Dockerfile uses Java 17"
  - name: "Actuator" icon: "monitoring" description: "Health, info, metrics, env, prometheus endpoints exposed"

- name: "Data Layer"
  color: "#2196F3"
  components:
  - name: "PostgreSQL 15" icon: "postgresql" description: "Persistent storage with Flyway migrations (currently disabled)"
  - name: "Redis" icon: "redis" description: "Cache with 1 hour TTL, lettuce connection pool"

- name: "Messaging Layer"
  color: "#FF9800"
  components:
  - name: "RabbitMQ" icon: "rabbitmq" description: "AMQP messaging for inventory events (INCONSISTENT: other services use Kafka)"

# DockerFile[]
dockerFiles:
- service: "inventory-service"
  description: "Dockerfile using openjdk:17-jdk-slim - VERSION MISMATCH with build.gradle which specifies Java 23"
  content: |
    # Use an official OpenJDK runtime as a parent image
    FROM openjdk:17-jdk-slim

    # Set the working directory in the container
    WORKDIR /app

    # Copy the entire project structure
    COPY .. .

    # Install dependencies and build the specific project
    RUN ./gradlew :inventory-service:build -x test

    # Set the working directory to the inventory-service for the runtime
    WORKDIR /app/inventory-service

    # Expose the port the app runs on
    EXPOSE 8082

    # Run the jar file
    ENTRYPOINT ["java", "-jar", "build/libs/inventory-service-0.0.1-SNAPSHOT.jar"]

- service: "nginx"
  description: "Nginx reverse proxy — TLS :443, redirect :80, least_conn upstream inventory_backend"
  content: |
    image: nginx:1.27-alpine
    container_name: inventory-nginx
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl/nginx.crt:/etc/nginx/ssl/nginx.crt:ro
      - ./nginx/ssl/nginx.key:/etc/nginx/ssl/nginx.key:ro
    depends_on:
      inventory-service:
        condition: service_healthy
---
# Infrastructure

> **Compose:** Includes **inventory-service**, **nginx** (`inventory-nginx`), Prometheus, Loki, and Grafana. HTTPS terminates at Nginx; optional HTTP via host **8093**. Run `nginx/ssl/generate-certs.sh` before first compose up.

> **CRITICAL ISSUES:**
> 1. **Java Version Mismatch**: build.gradle specifies Java 23 (line 12: `JavaLanguageVersion.of(23)`) but Dockerfile uses `openjdk:17-jdk-slim`. This will cause runtime issues as compiled classes with Java 23 (class version 69) won't run on Java 17 (max class version 61).
> 2. **RabbitMQ vs Kafka Inconsistency**: inventory-service uses RabbitMQ (spring-boot-starter-amqp) while other services (address, auth, cart) use Kafka. This creates integration issues.
> 3. **docker-compose.yml**: Present with nginx + observability — reconcile Dockerfile / ports / Flyway with local runtime.
> 4. **Flyway Disabled**: Flyway is configured but `enabled: false` in application.yml (line 90), meaning database migrations won't run automatically.
> 5. **Port Mismatch**: Dockerfile exposes port 8082 but application.yml sets server.port=8083.

---

## Automated integration tests (CI-friendly stack)

When `./gradlew test` runs, the **`test`** Spring profile loads **`application-test.yml`**: H2 in-memory database (PostgreSQL compatibility mode), Flyway disabled, Hibernate schema create-drop, Redis/Kafka auto-config excluded, global rate limiting disabled. REST integration tests send **`Authorization: Bearer …`** JWTs built with **`IntegrationTestJwtSupport`** so **`JwtAuthenticationFilter`** and **`JwtTokenValidator`** execute without mocking Spring Security. See **`docs/project/generated/ProjectFeature.md`** for the full checklist.
