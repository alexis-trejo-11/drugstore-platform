# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file from the host machine to the container
COPY --from=build /app/build/libs/*.jar /app/ecommerce_cart-service.jar

# Expose the port the application runs on
EXPOSE 8081

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "ecommerce_cartr-service.jar"]
