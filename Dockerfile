# ================================
# STAGE 1 -> BUILD THE APPLICATION
# ================================

# Use Maven + Java 21 image for building the project
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Metadata labels
LABEL author="kumarV"
LABEL description="Production-ready URL Shortener"
LABEL version="1.0"

# Set working directory inside container
WORKDIR /app

# Copy only pom.xml first
# This helps Docker cache dependencies layer
COPY pom.xml .

# Download all Maven dependencies
# If pom.xml does not change, Docker reuses cached dependencies
RUN mvn dependency:go-offline

# Copy source code into container
COPY src ./src

# Build Spring Boot jar
# clean   -> removes old build files
# package -> creates jar file inside target/
# -DskipTests -> skips running tests for faster builds
RUN mvn clean package -DskipTests


# ==================================
# STAGE 2 -> CREATE RUNTIME IMAGE
# ==================================

# Use lightweight Java 21 runtime image
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy generated jar from builder stage
# Source: builder container
# Destination: current runtime container
COPY --from=builder /app/target/url-shortener-app.jar app.jar

# Command executed when container starts
ENTRYPOINT ["java", "-jar", "app.jar"]