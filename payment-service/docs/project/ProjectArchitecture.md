# Architecture
> Payment Service follows hexagonal architecture with DDD principles. Two aggregate roots (Payment and Sale) with rich business logic. Ports define contracts; adapters implement them. Design patterns include Factory Method, State Machine (enum-based), Port-Adapter, Domain Events, and Value Object. StripeGatewayAdapter is a STUB - needs implementation. Kafka dependency added but not implemented.

## Docker edge ingress (Nginx)

- **HTTPS edge:** Nginx terminates TLS on `:443` and redirects `:80` → HTTPS (`payment-nginx`).
- **Upstream:** `payment_backend` with `least_conn` to `payment-service:8080`.
- **Direct HTTP:** Host mapping `8085:8080` for Swagger or debugging without TLS.

<!--
  OBSERVATIONS FOR ProjectArchitecture:
  ✅ POSITIVE:
    - Clean hexagonal architecture with 5 well-defined layers
    - 8 design patterns identified and documented (DDD, GoF, and custom)
    - Dual aggregate roots with separate state machines
    - State machines implemented in PaymentStatus (6 states) and SaleStatus (4 states) enums
    - Domain events (PaymentCompletedEvent, PaymentFailedEvent) for loose coupling
    - Architecture diagram with 8 nodes and 7 connections defined
    - Data flow documented for both request/response and event flows
    - 8 tech decisions documented with alternatives and outcomes
    - Strongly-typed value objects for type safety
    - Factory methods (create, fromPayment, reconstruct) for object creation

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - StripeGatewayAdapter IS A STUB - all methods return null or empty strings!
    - PaymentApplicationServiceImpl NOT FOUND - only interface exists!
    - Kafka dependency in build.gradle but NO Kafka code implemented
    - NO SecurityConfig - "Security placeholder" in controllers, no JWT/auth configured
    - API Gateway shown in architecture diagram but NOT IMPLEMENTED (marked as "Planned")
    - Stripe API connection (conn-4) marked as INACTIVE - adapter is a stub!
    - Spring Boot Actuator NOT in build.gradle dependencies (only Admin Client)
    - No circuit breaker pattern implemented for resilience
    - No API versioning strategy documented beyond "/api/v1" path
    - Shared kernel library (io.github.alexistrejo11:shared-kernel:1.0.0) dependency not versioned with project
    - SaleController says "Sales are read-only" but no enforcement at API level
    - No CORS configuration documented
    - Service runs on HTTP (port 8085) - NO SSL/HTTPS configured (unlike order-service)
    - HikariCP max-lifetime: 1200000ms (20 minutes) - verify this is correct (typo? Should be 120000?)
-->
