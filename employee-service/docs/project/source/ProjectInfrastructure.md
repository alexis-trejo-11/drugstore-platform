---
# InfrastructureMetric[]
metrics:
  - label: "Java Version"
    value: "23"
    icon: "java"
    description: "Running on Eclipse Temurin JDK 23 with Spring Boot 3.3.2"

  - label: "Database"
    value: "PostgreSQL 15"
    icon: "database"
    description: "Persistent storage with Flyway migrations (V1, V2)"

  - label: "Cache"
    value: "Redis"
    icon: "redis"
    description: "Used for rate limiting with libs-kernel"

  - label: "Config Server"
    value: "Spring Cloud Config"
    icon: "config"
    description: "Centralized configuration from config-server"

  - label: "Container Image"
    value: "PLACEHOLDER"
    icon: "docker"
    description: "PLACEHOLDER: Dockerfile not found in employee-service"

  - label: "HTTPS Port"
    value: "443 (Nginx)"
    icon: "lock"
    description: "TLS terminated by Nginx at :443, proxied internally to employee-service:8081"

  - label: "Health Check"
    value: "/actuator/health"
    icon: "heart"
    description: "Spring Boot Actuator (PLACEHOLDER: verify)"

  - label: "Reverse Proxy"
    value: "Nginx 1.27"
    icon: "nginx"
    description: "HTTP→HTTPS redirect on :80, TLS termination on :443, least_conn load balancing"

# CloudService[]
cloudServices:
  - name: "PLACEHOLDER: AWS RDS"
    purpose: "Managed PostgreSQL for production employee data"
    icon: "aws-rds"
    cost: "PLACEHOLDER: ~$30-100/month"

  - name: "PLACEHOLDER: AWS ElastiCache"
    purpose: "Managed Redis for production rate limiting"
    icon: "aws-elasticache"
    cost: "PLACEHOLDER: ~$20-80/month"

  - name: "PLACEHOLDER: Spring Cloud Config Server"
    purpose: "Centralized configuration management"
    icon: "spring-cloud"
    cost: "PLACEHOLDER: Free (self-hosted)"

  - name: "PLACEHOLDER: AWS ECS/EKS"
    purpose: "Container orchestration"
    icon: "aws-ecs"
    cost: "PLACEHOLDER: ~$30-150/month"

# DeploymentLayer[]
deploymentLayers:
  - name: "Client Layer"
    color: "#4CAF50"
    components:
      - name: "Admin Frontend"
        icon: "react"
        description: "Admin UI for employee management"
      - name: "Config Server"
        icon: "config"
        description: "Spring Cloud Config Server (from parent dir)"

  - name: "Application Layer"
    color: "#2196F3"
    components:
      - name: "Employee Service (Spring Boot)"
        icon: "spring"
        description: "Java 23 + Spring Boot 3.3.2 with CQS"
      - name: "Command Controller"
        icon: "controller"
        description: "EmployeeCommandController for write operations"
      - name: "Query Controller"
        icon: "controller"
        description: "EmployeeQueryController for read operations"

  - name: "Reverse Proxy / Load Balancer Layer"
    color: "#009688"
    components:
      - name: "Nginx 1.27"
        icon: "nginx"
        description: "TLS edge, redirects HTTP to HTTPS, and routes to employee-service replicas over internal HTTP :8081"

  - name: "Data Layer"
    color: "#9C27B0"
    components:
      - name: "PostgreSQL 15"
        icon: "postgres"
        description: "Employees, Certifications tables with indexes"
      - name: "Flyway Migrations"
        icon: "flyway"
        description: "V1__create_tables.sql, V2__insert_dummy_data.sql"

  - name: "Cache Layer"
    color: "#FF9800"
    components:
      - name: "Redis"
        icon: "redis"
        description: "Rate limiting with libs-kernel"

# DockerFile[]
dockerFiles:
  - service: "employee-service"
    description: "PLACEHOLDER: Dockerfile not found in employee-service root"
    content: |
      # PLACEHOLDER: Dockerfile needs to be created
      # Reference dockerfile from address-service or cart-service for template
      # Should use eclipse-temurin:23-jdk-alpine for build
      # Should use eclipse-temurin:23-jre-alpine for runtime
      # Include shared-kernel library build step
      # Copy keystore.p12 for HTTPS (if applicable)
      # Health check on appropriate port (check application.yml)

  - service: "nginx"
    description: "Nginx reverse proxy and load balancer for employee-service"
    content: |
      image: nginx:1.27-alpine
      container_name: employee-nginx
      ports:
        - "80:80"
        - "443:443"
      volumes:
        - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
        - ./nginx/ssl/nginx.crt:/etc/nginx/ssl/nginx.crt:ro
        - ./nginx/ssl/nginx.key:/etc/nginx/ssl/nginx.key:ro
      depends_on:
        employee-service:
          condition: service_started
---

# Infrastructure

> Employee stack now includes Nginx as reverse proxy and load balancer. External traffic enters via HTTPS on :443 at Nginx and is proxied internally to employee-service on HTTP :8081. HTTP :80 is redirected to HTTPS. This enables horizontal scaling of employee-service replicas behind a stable TLS endpoint.

Check and validate nginx i just shutdown my brain while dockerization, need to assert https at connection to front or portafolio
