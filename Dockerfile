# Use an official Gradle image to build the JAR
FROM gradle:7.5-jdk17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy Gradle wrapper files and source code into the container for each service
COPY client-service/gradle /app/gradle/
COPY client-service/build.gradle client-service/settings.gradle /app/
COPY client-service/src /app/src/

COPY employee-service/gradle /app/gradle/
COPY employee-service/build.gradle employee-service/settings.gradle /app/
COPY employee-service/src /app/src/

COPY inventory-service/gradle /app/gradle/
COPY inventory-service/build.gradle inventory-service/settings.gradle /app/
COPY inventory-service/src /app/src/

COPY product-service/gradle /app/gradle/
COPY product-service/build.gradle product-service/settings.gradle /app/
COPY product-service/src /app/src/

COPY sale-service/gradle /app/gradle/
COPY sale-service/build.gradle sale-service/settings.gradle /app/
COPY sale-service/src /app/src/

COPY user-service/gradle /app/gradle/
COPY user-service/build.gradle user-service/settings.gradle /app/
COPY user-service/src /app/src/

COPY ecommerce_cart-service/gradle /app/gradle/
COPY ecommerce_cart-service/build.gradle ecommerce_cart-service/settings.gradle /app/
COPY ecommerce_cart-service/src /app/src/

COPY ecommerce_order-service/gradle /app/gradle/
COPY ecommerce_order-service/build.gradle ecommerce_order-service/settings.gradle /app/
COPY ecommerce_order-service/src /app/src/

COPY ecommerce_payment-service/gradle /app/gradle/
COPY ecommerce_payment-service/build.gradle ecommerce_payment-service/settings.gradle /app/
COPY ecommerce_payment-service/src /app/src/

COPY ecommerce_sale-service/gradle /app/gradle/
COPY ecommerce_sale-service/build.gradle ecommerce_sale-service/settings.gradle /app/
COPY ecommerce_sale-service/src /app/src/

# Build the JAR file using Gradle
RUN gradle build --no-daemon

# Use a smaller OpenJDK image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/build/libs/*.jar /app/service.jar

# Expose the port on which the application runs
EXPOSE 8080

# Define the command to run the JAR file
ENTRYPOINT ["java", "-jar", "service.jar"]
