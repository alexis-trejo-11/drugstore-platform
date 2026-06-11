# Overview

> Comprehensive inventory service with batch tracking, reservations, and stock movements. Uses RabbitMQ (not Kafka like other services - inconsistency). Dockerfile uses openjdk:17-jdk-slim (not Eclipse Temurin like other services). **Testing:** Spring Boot integration tests run under profile `test` (H2, real JWT headers); see `docs/project/ProjectFeature.md`. **Still missing:** broader unit coverage, migrate to Kafka for consistency, Kubernetes manifests, CI/CD pipeline.
