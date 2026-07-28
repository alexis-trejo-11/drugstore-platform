# Build context: service root.
#   docker compose up -d --build
# glibc-based JDK: protoc-gen-grpc-java publishes linux binaries linked against glibc (Alpine/musl cannot run them).
# JDK 23 official images use Ubuntu Noble (no 23-jdk-jammy tag).
FROM eclipse-temurin:23-jdk-noble AS builder

# GitHub Packages — passed from docker compose build.args (reads host .env); not copied into the image.
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
RUN mkdir -p /app/config && chown -R spring:spring /app

RUN echo '#!/bin/sh' > /app/entrypoint.sh && \
    echo 'exec su-exec spring:spring java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} -jar /app/app.jar "$@"' >> /app/entrypoint.sh && \
    chmod +x /app/entrypoint.sh

USER root
RUN apk add --no-cache su-exec wget

ENTRYPOINT ["/app/entrypoint.sh"]