# Overview

This document is the **plain-Markdown twin** of `docs/project/obsidian/ProjectOverview.md` (same facts, no Obsidian-only callouts).

---

## Problem statement

**Title:** Centralized store master data for a distributed drugstore platform.

**Description:** Channels and other microservices need authoritative **store identity**, **address**, **geolocation**, **schedule**, and **operational status** with predictable APIs and operational guardrails.

**Problems addressed**

- Avoid duplicating store rows across services.
- Support **read-heavy** storefront search with caching.
- Offer **role-gated mutations** for admins while keeping read paths fast.

---

## Solution

**Title:** Store Service — hexagonal REST API on Spring Boot.

**Approach**

1. **Ports & adapters:** REST controllers translate to use cases; JPA implements `StoreRepository`; messaging port exists with a stub adapter.
2. **Cross-cutting:** Redis cache, JWT security, method-level rate limiting (libs_kernel), Flyway migrations, OpenAPI.
3. **Operations:** Docker Compose with Postgres, Redis, Prometheus, Loki, Grafana, and Nginx for TLS at the edge.

---

## Key metrics (placeholder)

- **SLOs:** Not formalized; define p95 latency and error budgets per environment.
- **Cache:** Monitor Redis memory and hit ratio once metrics dashboards exist in Grafana (provisioning path: `observability/grafana/provisioning/`).

---

## Links & media (placeholders)

| Link | URL |
|------|-----|
| Repository | `https://github.com/alexisTrejo11/drugstore-platform` |
| Docs (this tree) | `store-service/docs/project` |
| Demo | *TBD* |
| Docker Hub | *TBD* |

Cover image: use `https://placehold.co/1200x630/1a237e/ffffff?text=Store+Service` until a real asset exists.

---

## Risk register (short)

| Severity | Item |
|----------|------|
| High | **application.yml** mislabels the service and DB for `dev` profile. |
| High | **Redis Jackson default typing** — review for untrusted-content / RCE class of issues. |
| Medium | **Swagger vs SecurityConfig** mismatch on GET (anonymous vs documented bearer). |
| Medium | **No rate limit** on `PATCH .../temporary-closure` and `PATCH .../deactivate`. |
| Medium | **Kafka publisher** is a no-op but broker env is mandatory in Compose. |

---

## Version & API surface

- **Service version:** 2.0.0 (`build.gradle`).
- **HTTP API:** `/api/v2/stores` (commands and queries).
- **Documentation:** `/swagger-ui.html` and `/v3/api-docs` when enabled.
