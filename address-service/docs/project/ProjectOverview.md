# Project Overview

## Problem Statement
### Address Management Complexity in Multi-Country E-Commerce
E-commerce platforms require robust address management supporting multiple countries with different postal code formats, role-based access (customers vs employees), default address handling, and limits per user type. Without a dedicated service, address logic couples to user management and lacks validation.

### Main Problems
- No centralized validation for multi-country postal codes (US, MX, CA, ES, UK).
- No role-based address limits (customer vs employee).
- No deterministic default-address flow.
- Missing rate limiting and stronger security controls.
- Address logic spread across services.

## Solution
### Dedicated Address Microservice with Multi-Country Validation
- **Multi-country postal validation:** Factory + strategy for per-country format checks.
- **Role-based limits:** CUSTOMER up to 5, EMPLOYEE up to 1.
- **Default address handling:** first address can become default; only one default per user.
- **Dual-controller model:** user routes and admin routes are separated.
- **Redis-backed rate limiting:** profile-based limits for read/write sensitivity.

## Key Metrics
- Supports 5+ country-specific postal validators.
- Role limits enforced by user type.
- 10 REST endpoints for core address operations.
- DDoS/abuse mitigation through Redis rate limiting.
- Indexed PostgreSQL queries for common lookups.

## Links
- **GitHub:** [address-service](https://github.com/alexisTrejo11/drugstore-platform/tree/main/address-service)
- **Docs (Swagger):** https://api.ecommerce.com/address-service/swagger-ui
- **Docker Hub:** https://hub.docker.com/r/alexistrejo11/address-service

## Project Metrics
- **API Endpoints:** 10
- **Countries Supported:** 5+
- **Average Response Time (p95):** <50ms

## Media (References)
- Swagger UI screenshot
- Architecture diagram
- Postman test screenshot
