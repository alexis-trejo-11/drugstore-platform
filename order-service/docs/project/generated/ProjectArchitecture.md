# Architecture
> Order Service follows hexagonal architecture with DDD principles. The domain layer contains the Order aggregate root with rich business logic. Ports define contracts; adapters implement them. Design patterns include Decorator (caching/logging), Facade (unified interface), Builder (object creation), and State (order status transitions). The service uses PostgreSQL, Redis, and OpenSearch/ELK stack.

## Docker edge ingress (Nginx)

- **TLS termination:** Clients use HTTPS on host `:443`; `:80` redirects to HTTPS (`nginx:1.27-alpine`, service `nginx` in compose).
- **Load balancing:** Upstream `order_backend` uses `least_conn`; scale app replicas with `docker compose up --scale order-service=N`.
- **Hop to Spring Boot:** Nginx proxies over HTTP to `order-service:8080` on the compose network (TLS stops at Nginx). Optional direct HTTP via published host port `8086:8080` for local tooling.

<!--
  OBSERVATIONS FOR ProjectArchitecture:
  ✅ POSITIVE:
    - Clean hexagonal architecture with 5 well-defined layers
    - 9 design patterns identified and documented (DDD, GoF, and custom)
    - Rich domain model with aggregate root pattern
    - State machine implemented in OrderStatus enum
    - Specification pattern for dynamic queries
    - Architecture diagram with 8 nodes and 7 connections defined
    - Data flow documented for both request/response and event flows
    - 8 tech decisions documented with alternatives and outcomes

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - API Gateway shown in architecture diagram but NOT IMPLEMENTED (marked as "Planned")
    - Event Bus node in diagram but actual implementation is "planned" - EventPublisher port has no real implementation
    - "other-services" node is vague - no specific service names or connections defined
    - Architecture diagram coordinates (x,y) are approximate - may need adjustment for rendering
    - build.gradle has DUPLICATE bootRun configuration (lines 88-106 and 114-132)
    - No circuit breaker pattern implemented for resilience
    - No API versioning strategy documented beyond "/api/v2" path
    - Shared kernel library (io.github.alexistrejo11:shared-kernel:1.0.0) dependency not versioned with project
-->
