# Build context: user-service/ (run from project root:
#   docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build)
# glibc-based JDK: protoc-gen-grpc-java publishes linux binaries linked against glibc (Alpine/musl cannot run them).
FROM eclipse-temurin:23-jdk-noble AS builder

ARG GITHUB_ACTOR
ARG GITHUB_TOKEN
ENV GITHUB_ACTOR=$GITHUB_ACTOR
ENV GITHUB_TOKEN=$GITHUB_TOKEN

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

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder /app/build/libs/*.jar app.jar
RUN mkdir -p /app/logs /app/config
RUN chown -R spring:spring /app

RUN echo '#!/bin/sh' > /app/entrypoint.sh && \
    echo 'chown -R spring:spring /app/logs 2>/dev/null || true' >> /app/entrypoint.sh && \
    echo 'exec su-exec spring:spring java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} -Dlogging.file.path=/app/logs -jar /app/app.jar "$@"' >> /app/entrypoint.sh && \
    chmod +x /app/entrypoint.sh

USER root
RUN apk add --no-cache su-exec wget

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["/app/entrypoint.sh"]
