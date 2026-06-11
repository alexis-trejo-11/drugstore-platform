# Project Features
> Payment Service provides 13+ features including dual aggregate roots (Payment and Sale), 6-state payment lifecycle, Stripe webhook handling, domain events, and strongly-typed value objects. The service follows DDD principles with hexagonal architecture. Stripe integration is planned but StripeGatewayAdapter is currently a STUB.

<!--
  OBSERVATIONS FOR ProjectFeature:
  ✅ POSITIVE:
    - 13 well-documented features covering all aspects of payment processing
    - Dual aggregate roots (Payment and Sale) with separate lifecycles
    - Clean hexagonal architecture with ports and adapters
    - Domain events (PaymentCompletedEvent, PaymentFailedEvent) for loose coupling
    - Strongly-typed value objects for type safety
    - Comprehensive webhook handling with Stripe signature verification
    - Flyway migrations with JPA validation mode
    - HikariCP connection pooling configured
    - OpenAPI documentation with springdoc-openapi

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - StripeGatewayAdapter IS A STUB - all methods return null or empty strings (lines 11, 16, 21)!
    - Stripe integration "Planned" but no actual Stripe SDK dependency in build.gradle
    - PaymentApplicationServiceImpl NOT FOUND - only interface exists, no implementation!
    - No unit/integration test coverage mentioned in features
    - Kafka dependency in build.gradle but NO Kafka code implemented
    - No SecurityConfig - controllers have "Security placeholder" comments
    - Spring Boot Actuator NOT in build.gradle but referenced in comments
    - No rate limiting configured (mentioned as "placeholder" in controllers)
    - SaleController says "Sales are read-only from API perspective" but no enforcement
    - Refund flow depends on StripeGatewayAdapter which is NOT implemented
    - No notification feature - customers not notified on payment completion/failure
    - PaymentMethod enum has 6 methods but Stripe may not support all (e.g., CASH, INTERNAL_CREDIT)
-->
