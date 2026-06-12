# Build context: inventory-service root (run from project root):
#   docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
# glibc-based JDK: protoc-gen-grpc-java publishes linux binaries linked against glibc (Alpine/musl cannot run them).
# JDK 23 official images use Ubuntu Noble (no 23-jdk-jammy tag).
FROM eclipse-temurin:23-jdk-noble AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .

RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew bootJar --no-daemon


# Execution stage
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring \
    && apk add --no-cache wget

COPY --from=builder --chown=spring:spring /app/build/libs/*.jar app.jar

# Logback RollingFileAppender writes under LOG_PATH (default logs/ → /app/logs)
RUN mkdir -p /app/logs && chown spring:spring /app/logs

USER spring

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
