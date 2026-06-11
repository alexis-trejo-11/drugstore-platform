# Project Architecture

## Layered Architecture

### Controllers (Inbound)
- Handles REST requests and response wrapping.
- Applies JWT auth, role checks, and rate-limit annotations.
- Main components: `UserAddressController`, `AddressAdminController`.

### Service Layer
- Central business orchestration and transactions.
- Includes validation, mapping, and domain rules.
- Main component: `AddressService`.

### Validation Layer
- Country-specific postal-code validation.
- Factory and strategy patterns for extensibility.

### Data Access Layer
- JPA repositories and entity persistence.
- PostgreSQL + Flyway schema management.

### Configuration Layer
- Security config, CORS, rate-limit infra, OpenAPI, global errors.

## Design Patterns
- Factory Pattern (validator creation)
- Strategy Pattern (country validators)
- DTO Pattern (request/response contracts)
- Repository Pattern (data abstraction)
- Builder Pattern (entity creation)

## Scalability Strategies
- Nginx `least_conn` load balancing — scale with `docker compose up --scale address-service=N`
- Stateless JWT architecture for horizontal scaling
- Connection pooling
- Distributed rate limiting through Redis
- Pageable admin queries

## Security Strategies
- Nginx TLS termination at the edge; internal Docker traffic uses HTTPS with `proxy_ssl_verify off`
- JWT bearer authentication
- Role-based access control
- Redis rate limiting
- DTO/input validation
- HTTPS/SSL (port 8443 internal only, not exposed to host)
- Controlled CORS

## Cache Strategy
- Redis counters with TTL for rate-limit windows.

## Architecture Highlights
- Multi-country address validation
- Dual user/admin control plane
- Soft-delete persistence model
- Structured audit-friendly logging

## Data Flow (Request Path)
1. Client sends HTTPS to Nginx `:443` — TLS terminated, least_conn routing to a replica
2. Nginx forwards request to address-service `:8443` (internal Docker network)
3. Authentication filter validates JWT Bearer token
4. Rate-limit check (Redis)
5. Controller action
6. Service orchestration
7. Repository persistence/query
8. Wrapped API response back through Nginx to client

## Key Technical Decisions
- Factory + strategy for postal code validation.
- Soft delete to preserve historical consistency.
- Separate user/admin controllers for clean authorization boundaries.
- Redis + AOP for centralized, distributed rate limiting.
- UUID IDs for non-sequential, non-guessable identifiers.
