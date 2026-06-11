# Project Architecture

## Layered Architecture
- Controllers expose employee APIs.
- Application layer handles command/query orchestration.
- Persistence layer stores employee data in PostgreSQL.
- Redis supports cross-cutting concerns (rate limit/cache).
- Nginx is the edge ingress and load-balancer in Docker deployments.

## Scalability Strategies
- Nginx `least_conn` + Docker DNS for horizontal scale (`--scale employee-service=N`)
- Stateless service instances behind reverse proxy

## Security Strategies
- TLS terminated at Nginx on `:443`
- HTTP to HTTPS redirect at Nginx on `:80`
- JWT + role checks remain enforced in employee-service

## Request Flow
1. Client sends request to Nginx (`https://localhost`)
2. Nginx terminates TLS and routes to `employee-service:8081`
3. Service executes auth, business logic, and persistence operations
4. Response returns through Nginx

