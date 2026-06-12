# Build context: payment-service root (run from docker/: docker compose -f docker-compose.full.yml --profile local up -d --build)
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

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
