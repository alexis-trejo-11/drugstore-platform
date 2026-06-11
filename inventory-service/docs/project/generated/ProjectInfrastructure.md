# Infrastructure

## Reverse proxy (Nginx)

`docker-compose.yml` includes **nginx** (`nginx:1.27-alpine`, container `inventory-nginx`). Public HTTPS on host `:443` and HTTP→HTTPS on `:80`. Upstream `inventory_backend` uses `least_conn` toward `inventory-service:8080`. Host port `8093:8080` still exposes plain HTTP for direct dev calls. Run `nginx/ssl/generate-certs.sh` before first compose run.

---

> **CRITICAL ISSUES:**
> 1. **Java Version Mismatch**: build.gradle specifies Java 23 (line 12: `JavaLanguageVersion.of(23)`) but Dockerfile uses `openjdk:17-jdk-slim`. This will cause runtime issues as compiled classes with Java 23 (class version 69) won't run on Java 17 (max class version 61).
> 2. **RabbitMQ vs Kafka Inconsistency**: inventory-service uses RabbitMQ (spring-boot-starter-amqp) while other services (address, auth, cart) use Kafka. This creates integration issues.
> 3. **docker-compose.yml**: Present with nginx + observability; align Dockerfile/Java/Flyway notes below with runtime expectations.
> 4. **Flyway Disabled**: Flyway is configured but `enabled: false` in application.yml (line 90), meaning database migrations won't run automatically.
> 5. **Port Mismatch**: Dockerfile exposes port 8082 but application.yml sets server.port=8083.

---

## Automated integration tests (CI-friendly stack)

When `./gradlew test` runs, the **`test`** Spring profile loads **`application-test.yml`**: H2 in-memory database (PostgreSQL compatibility mode), Flyway disabled, Hibernate schema create-drop, Redis/Kafka auto-config excluded, global rate limiting disabled. REST integration tests send **`Authorization: Bearer …`** JWTs built with **`IntegrationTestJwtSupport`** so **`JwtAuthenticationFilter`** and **`JwtTokenValidator`** execute without mocking Spring Security. See **`docs/project/generated/ProjectFeature.md`** for the full checklist.
