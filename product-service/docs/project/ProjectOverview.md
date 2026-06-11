# Overview

## Problem

Drugstore platform needs one canonical product-catalog owner that keeps product data consistent, secure for writes, fast for reads, and easy to integrate across services.

### Key pain points

- Inconsistent product data without a single owning service.
- Need JWT role protection for write operations.
- Read-heavy lookups require caching to protect DB throughput.
- Product changes must propagate to downstream domains.

## Solution

Product Service implements a Spring Boot microservice with:

- REST API for product CRUD/search/category lookup.
- Domain-level validation and role-based authorization.
- Redis-backed query caching using use-case decorators.
- Kafka product event publication.

## Key Metrics

- 8 REST endpoints under `/api/v2/products`
- 4 cache regions
- 2 Kafka topic names configured (`product-events`, `product-events-dlt`)
- 7 integration tests in `ProductApiIntegrationTest`

## Media and Links (placeholders)

- Cover image: `https://placehold.co/1200x630?text=Drugstore+Product+Service`
- Demo URL: `https://placeholder.drugstore.local/product-service-demo`
- Docker Hub URL: `https://hub.docker.com/r/placeholder/product-service`

## Notes

- Configuration has mixed env var names (`product_DB` vs `POSTGRES_DB_NAME`) that can cause runtime misconfiguration.
- Swagger and broad actuator exposure are enabled by default; tighten for non-dev environments.