# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder

# Install Maven
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Set working directory
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8081

# Set JVM options
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]