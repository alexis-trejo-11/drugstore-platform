# Project Architecture

## Layered Architecture
- Controllers expose REST and gRPC endpoints for cart operations.
- Application services execute command/query use cases.
- Domain layer encapsulates cart aggregate invariants.
- Infrastructure adapters handle persistence, Kafka, and cache.
- Configuration layer centralizes security, OpenAPI, and platform cross-cutting concerns.

## Scalability Strategies
- Nginx `least_conn` load balancing — scale with `docker compose up --scale cart-service=N`
- Stateless JWT security context in service instances
- Redis caching for repeated cart reads
- gRPC for low-latency order-service integration
- Pageable admin search support

## Security Strategies
- Nginx TLS termination on `:443` and forced HTTP→HTTPS redirect on `:80`
- JWT authentication with role-based route protection
- Internal HTTPS between Nginx and cart-service (`proxy_ssl_verify off` for Docker private network)
- Container HTTPS on `:8443` not exposed directly to host

## Data Flow (Request Path)
1. Client sends HTTPS request to Nginx (`:443`)
2. Nginx terminates TLS and forwards to cart-service (`:8443`) using least-connection policy
3. JWT authentication and authorization checks run in cart-service
4. Use case execution applies domain rules
5. PostgreSQL/Redis/Kafka interactions occur as needed
6. Response returns through Nginx to client

## Notes
- CartPurchasedEvent remains a future/pending publication path.
- Rate limit annotations on controllers are still pending alignment with address/auth patterns.
