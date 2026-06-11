# Project Metadata

| Field | Value |
|-------|--------|
| **projectId** | `user-service` |
| **name** | User Service |
| **version** | `2.0.0` (Gradle) |
| **language / runtime** | Java **23** (Temurin in Docker) |
| **framework** | Spring Boot **3.5.14** |
| **category** | backend microservice |
| **status** | develop |
| **repositoryUrl** | `https://github.com/PLACEHOLDER/drugstore-platform/tree/main/user-service` (dummy) |
| **liveDemoUrl** | *(null / not hosted)* |

## Tech stack

- Spring Web, Security, Validation, Actuator  
- Spring Data JPA • PostgreSQL  
- Spring Data Redis • Spring cache → Redis  
- Spring Kafka (+ Jackson JSON events)  
- Flyway (**enabled only when profile YAML is corrected** — docker file today is contaminated)  
- grpc-java **1.60.0** • protobuf **3.25.1**  
- JJWT • SpringDoc OpenAPI **2.6.0**  
- Micrometer Prometheus • Loki logback appender  
- Spring Boot Admin client • Docker / Compose  

## Dangerous / misleading / outdated notes

1. **Older internal docs claiming OpenJDK 17 + Boot 3.3.2** — **incorrect** today; Dockerfile and `build.gradle` use **JDK 23** and **Boot 3.5.14**.  
2. **`spring.application.name` in `application-docker.yml`** reads **store-service** → **critical misconfiguration**.  
3. **`application.yml` logging**: file name **`products-service.log`** + logger packages referencing **products** — copy-paste **noise**.  
4. **`ORDER_SERVICE_HOST_PORT`** env var publishes **user** HTTP (`default host 8086`) — naming is **misleading**, not harmless if ops scripts assume order-service.  
5. **JWT**: Compose requires **`JWT_SECRET_KEY`** — good — but **dev YAML** ships **placeholder secret**.  
6. **Kafka deserialization**: **`spring.json.trusted.packages: '*'`** reduces safety — replace with explicit packages in hardened envs.
