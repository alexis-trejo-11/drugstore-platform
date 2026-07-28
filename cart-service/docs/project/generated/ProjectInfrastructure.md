# Project Infrastructure

## Infrastructure Metrics
- **Java Version:** 23 (Eclipse Temurin, Spring Boot 3.3.2)
- **Database:** PostgreSQL 15 with Flyway migrations
- **Cache:** Redis 7 (Spring Cache for cart lookups)
- **gRPC:** 1.60.0 (Protobuf 3.25.1 for order-service communication)
- **Kafka:** `product-events` topic integration
- **Container Runtime:** `eclipse-temurin:23-jre-alpine`
- **HTTPS Port:** `8443` (internal only — all traffic enters through Nginx)
- **Reverse Proxy:** Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).
- **Health Endpoint:** `/actuator/health`

## Cloud/Service Components
- AWS RDS (placeholder for managed PostgreSQL)
- AWS ElastiCache (placeholder for managed Redis)
- AWS MSK (placeholder for managed Kafka)
- AWS ECS/EKS (placeholder for orchestration)

## Deployment Layers
### Client Layer
- Frontend application
- Order service (gRPC consumer)

### Reverse Proxy / Load Balancer Layer
- Edge TLS/reverse proxy is provided by shared infra outside this monorepo (not bundled per service).

### Application Layer
- Cart Service (Spring Boot, DDD)
- Cart aggregate root and use case orchestration

### Data Layer
- PostgreSQL 15
- Flyway migrations

### Cache Layer
- Redis 7

### Event Layer
- Kafka product-events consumers

## Docker Services
### cart-service
Multi-stage Docker build with shared-kernel compilation. Port 8443 is internal-only via `expose:` and intended to be reached through Nginx.

### nginx

### postgres
PostgreSQL 15 container with persistent volume and healthcheck.

### redis
Redis 7 container with persistence and healthcheck.

### prometheus / loki / grafana
Observability stack for metrics, logs, and dashboards.
