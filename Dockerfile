# Multi-stage build for Prompt Evaluation System
# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Copy gradle wrapper files
COPY gradlew ./
COPY gradle ./gradle

# Ensure gradlew has execute permissions
RUN chmod +x ./gradlew

# Copy gradle files first for better layer caching
COPY build.gradle settings.gradle ./

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew bootJar --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Create non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring

# Copy JAR from builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Create directory for SQLite database with proper permissions
RUN mkdir -p /app/data && chown -R spring:spring /app

# Switch to non-root user
USER spring

# Expose port
EXPOSE 18080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:18080/ || exit 1

# Run the application
# SQLite database will be stored at /app/data/prompt_ev.db
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.datasource.url=jdbc:sqlite:/app/data/prompt_ev.db"]
