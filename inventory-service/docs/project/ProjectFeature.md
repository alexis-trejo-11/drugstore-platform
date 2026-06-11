# Project Features

## Integration testing (`test` profile)

Automated API integration tests exercise the HTTP stack end-to-end with a dedicated Spring profile:

| Area | Detail |
|------|--------|
| **Profile** | `test` — activated via `src/test/resources/application.yml` (`spring.profiles.active=test`). |
| **Config** | `src/test/resources/application-test.yml`: H2 in-memory (`MODE=PostgreSQL`), Hibernate `ddl-auto: create-drop`, Flyway disabled for speed and portability. |
| **Authentication** | **Real JWT path**: tokens signed with the same `jwt.secret` as the test YAML are sent as `Authorization: Bearer …`; requests pass through `JwtAuthenticationFilter` and `JwtTokenValidator` (no mocked security filter chain). |
| **Isolation** | Redis and Kafka auto-configuration excluded; `app.rate-limit.global.enabled: false`; Spring cache type `simple`. |
| **Main suite** | `InventoryApiIntegrationTest`: `@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional` — inventory create → GET by id/product, PATCH settings, low-stock pagination, and auth behaviour (401 without token, 403 insufficient role, invalid JWT). |
| **JWT helper** | `IntegrationTestJwtSupport` builds HS256 access tokens with claims `userId`, `role`, `type=access`. |
| **Smoke** | `InventoryItemServiceImplApplicationTests` loads the Spring context under `test`. |

Run: `./gradlew test` (from `inventory-service`).

---

> **CRITICAL OBSERVATIONS:**
> 1. **RabbitMQ vs Kafka Inconsistency**: inventory-service uses RabbitMQ (spring-boot-starter-amqp) while all other services (address, auth, cart) use Kafka. This PREVENTS direct messaging integration.
> 2. **Java Version Mismatch**: build.gradle specifies Java 23 but Dockerfile uses openjdk:17-jdk-slim. This will cause `UnsupportedClassVersionError` at runtime.
> 3. **Flyway Disabled**: Database migrations are configured but disabled (`flyway.enabled: false`). This means schema changes won't be tracked or auto-applied.
> 4. **docker-compose.yml**: Present for local stacks (service + Postgres + Redis + nginx + observability); align Dockerfile/Java/Flyway with how you run locally.
> 5. **No gRPC Endpoints**: Other services expose gRPC for inter-service communication, but inventory-service only has REST endpoints.
> 6. **PLACEHOLDER Metrics**: Actual metric values (cache hit rate, response times, etc.) need to be filled in with real monitoring data.
> 7. **Expiration Alerts Status**: Marked as "beta" because the scheduled job to auto-mark expiring batches may not be implemented yet (only manual endpoints exist).
