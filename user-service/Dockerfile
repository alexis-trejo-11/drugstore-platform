# Use an official Gradle image to build the JAR
FROM gradle:7.5-jdk17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy Gradle wrapper files and source code into the container
COPY gradle/gradle-wrapper.jar gradle/
COPY gradle/gradle-wrapper.properties gradle/
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

# Build the JAR file using Gradle
RUN gradle build --no-daemon

# Use a smaller OpenJDK image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/build/libs/*.jar /app/user-service.jar

# Expose the port on which the application runs
EXPOSE 8081

# Define the command to run the JAR file
ENTRYPOINT ["java", "-jar", "user-service.jar"]
