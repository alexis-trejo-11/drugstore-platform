# Architecture

## Layers

### Inbound Adapters (Web/API)

- Controller + DTO mappers for `/api/v2/products`.
- Validates input and delegates to use-cases.

### Application/Core Use Cases

- Joined use-case facade for command and query operations.
- Decorator-based caching for read paths.

### Outbound Adapters

- JPA repository implementation for PostgreSQL.
- Kafka event publisher.
- Redis cache manager configuration.

## Design patterns

- Ports and Adapters (Hexagonal)
- Decorator for caching
- Soft delete with restore workflow

## Scalability strategies

- Horizontal scaling through compose replicas and nginx upstream.
- Cache-backed read optimizations.
- Event-driven decoupling via Kafka.

## Security strategies

- JWT stateless authentication.
- Role-based authorization (`ADMIN`, `MANAGER`) for writes.
- Rate-limit profiles for public and sensitive operations.

## Data/event flow

1. Client -> nginx (TLS termination)
2. nginx -> product-service (internal HTTP)
3. product-service -> PostgreSQL/Redis
4. product-service -> Kafka for lifecycle events

## Notes

- Package naming between `ratelimit` and `rate_limit` is inconsistent.
- Runtime audit event metadata currently reports wrong service name (`address-service`) in Product Service logs.

