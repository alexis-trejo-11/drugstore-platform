# API Schema

> 7 REST endpoints (4 user + 3 admin) and 2 gRPC methods documented. PLACEHOLDER issues: No @RateLimit annotations on any REST controllers (unlike address-service), CartPurchasedEvent defined but not published to Kafka. The service uses @PreAuthorize("hasRole('ADMIN')") on CartManagerController. Potential: Add OpenAPI annotations to all endpoints, implement rate limiting, publish cart events to Kafka.
