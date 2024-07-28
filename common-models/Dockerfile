# Use an official Gradle image to build the JARs
FROM gradle:7.5-jdk17 AS build

WORKDIR /app

COPY settings.gradle ./
COPY build.gradle ./
COPY common-models/gradle ./common-models/gradle/
COPY common-models/build.gradle ./common-models/build.gradle
COPY common-models/settings.gradle ./common-models/settings.gradle
COPY common-models/src ./common-models/src

RUN gradle :common-models:clean :common-models:build --no-daemon

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/common-models/build/libs/*.jar /app/common-models.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "common-models.jar"]
