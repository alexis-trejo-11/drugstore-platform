# Project Infrastructure

## Infrastructure Metrics
- **Java Version:** 23 (Eclipse Temurin, Spring Boot 3.3.2)
- **Database:** PostgreSQL 15 with Flyway migrations
- **Cache/Rate Limiter:** Redis 7 (token-bucket style controls)
- **Container Runtime:** `eclipse-temurin:23-jre-alpine`
- **HTTPS Port:** `8443`
- **Health Endpoint:** `/actuator/health`
- **Metrics Endpoint:** `/actuator/prometheus`
- **Observability Stack:** Prometheus + Loki + Grafana
- **Reverse Proxy:** Nginx 1.27 — TLS termination on `:443`, HTTP→HTTPS redirect on `:80`, `least_conn` load balancing

## Cloud/Service Components
- AWS RDS (placeholder for managed PostgreSQL)
- AWS ElastiCache (placeholder for managed Redis)
- AWS ECS/EKS (placeholder for orchestration)
- AWS Certificate Manager (placeholder for cert lifecycle)
- AWS CloudWatch (placeholder for managed monitoring)
- Grafana OSS (dashboards)
- Prometheus OSS (metrics scraping/storage)
- Loki OSS (log aggregation/query)

## Deployment Layers
### Client Layer
- Frontend application
- Mobile app (future)

### Application Layer
- Address Service (Spring Boot)
- JWT authentication filter from shared kernel

### Data Layer
- PostgreSQL 15
- Flyway migrations

### Cache Layer
- Redis 7

### Reverse Proxy / Load Balancer Layer
- Nginx 1.27 (TLS termination, HTTP→HTTPS redirect, least_conn upstream)

### Observability Layer
- Prometheus
- Loki
- Grafana

## Docker Services
### address-service
Multi-stage Docker build with JDK 23 (build) and JRE 23 (runtime), non-root execution, healthcheck enabled, HTTPS on `8443`.

### postgres
PostgreSQL 15 container with persistent volume and healthcheck.

### redis
Redis 7 container with persistence and healthcheck.

### prometheus
Scrapes service metrics from `/actuator/prometheus` using `observability/prometheus/prometheus.yml`.

### loki
Receives centralized logs from Logback (Loki4j appender).

### grafana
Dashboard and exploration UI with pre-provisioned Prometheus/Loki datasources.

### nginx
Nginx 1.27 reverse proxy. Terminates TLS on port `443` using self-signed certificates (dev). Redirects port `80` to HTTPS. Load-balances over `address_backend` upstream using `least_conn`. The address-service port 8443 is not bound to the host — all traffic enters through Nginx.
