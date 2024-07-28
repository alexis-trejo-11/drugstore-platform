# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the entire project structure
COPY .. .

# Install dependencies and build the specific project
RUN ./gradlew :ecommerce_order-service:build -x test

# Set the working directory to the ecommerce_order-service for the runtime
WORKDIR /app/ecommerce_order-service

# Expose the port the app runs on
EXPOSE 8082

# Run the jar file
ENTRYPOINT ["java", "-jar", "build/libs/ecommerce_order-service-0.0.1-SNAPSHOT.jar"]