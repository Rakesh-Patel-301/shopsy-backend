# Stage 1: Build JAR inside container
FROM maven:3.9.6-eclipse-temurin-17 as builder

WORKDIR /app

# Copy source code
COPY . .

# Build the JAR
RUN mvn clean package -DskipTests

# Stage 2: Run the built app
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/ecommerce-backend-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
