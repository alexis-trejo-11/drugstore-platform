# Admin Service

Admin Service hosts **Spring Boot Admin Server** for the Drugstore Platform: a single UI to inspect registered applications (health, metrics, loggers, environment) when each microservice includes `spring-boot-admin-starter-client`.

## Quick start

From `admin-service/` (adjust to your Gradle wrapper and main class if different):

```bash
./gradlew bootRun
```

Point client services at this server URL using their `spring.boot.admin.client.url` (see each service `application.yml`).

## Related documentation

- Client registration is configured per domain service (for example `user-service`, `product-service`, `order-service`).
- Platform overview: [root README](../README.md).

---

Add service-specific `docs/project/` here when you want Admin-only runbooks or operations docs.
