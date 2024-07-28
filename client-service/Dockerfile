# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim as builder

# Set the working directory in the builder container
WORKDIR /app

# Copy the entire project structure
COPY .. .

# Install dependencies and build the specific project
RUN ./gradlew :client-service:build -x test

# Use a minimal base image for the final stage
FROM openjdk:17-jdk-slim

# Set the working directory in the final container
WORKDIR /app/client-service

# Copy the built jar file from the builder stage
COPY --from=builder /app/client-service/build/libs/client-service-0.0.1-SNAPSHOT.jar ./build/libs/client-service-0.0.1-SNAPSHOT.jar

# Expose the port the app runs on
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "build/libs/client-service-0.0.1-SNAPSHOT.jar"]
