# Build context: payment-service root (run from project root):
#   docker compose -f docker/docker-compose.full.yml --env-file .env up -d --build
FROM eclipse-temurin:23-jdk-noble AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
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

RUN mkdir -p /app/logs && chown spring:spring /app/logs

USER spring

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} -jar /app/app.jar"]
