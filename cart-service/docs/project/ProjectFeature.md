# Project Features

> 12 comprehensive features documented covering DDD, gRPC, Kafka, caching, and design patterns. Has 11 unit test files for domain layer.
> 
> **Potential Issues & Improvements:**
> - No @RateLimit annotations on REST controllers (unlike address-service)
> - CartPurchasedEvent defined but not published to Kafka (PLACEHOLDER)
> - Java 23 may cause compatibility issues with some libraries
> - No integration tests for gRPC endpoints
> - @Cacheable annotations not applied to repository methods
> - Docker Compose missing Kafka dependency for product-events
> - Missing Kubernetes manifests for cloud deployment
> - No CI/CD pipeline (GitHub Actions/Jenkins)
> - Consider adding Circuit Breaker (Resilience4j) for external calls
> - Add Micrometer metrics for cart operations (add/update/clear rates)
