# Build context: service root.
#   docker compose up -d --build
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

FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring \
    && apk add --no-cache wget su-exec

COPY --from=builder /app/build/libs/*.jar app.jar

RUN mkdir -p /app/config \
    && chown -R spring:spring /app

RUN echo '#!/bin/sh' > /app/entrypoint.sh && \
  echo 'exec su-exec spring:spring java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} -jar /app/app.jar "$@"' >> /app/entrypoint.sh && \
  chmod +x /app/entrypoint.sh

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:${NOTIFICATION_SERVICE_PORT:-8093}/actuator/health || exit 1

EXPOSE 8093

ENTRYPOINT ["/app/entrypoint.sh"]
