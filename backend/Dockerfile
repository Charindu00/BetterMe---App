# ╔══════════════════════════════════════════════════════════════════════════╗
# ║ LEARNING POINT: Multi-Stage Dockerfile                                    ║
# ╠══════════════════════════════════════════════════════════════════════════╣
# ║ Multi-stage builds create SMALLER images by separating build and run.    ║
# ║                                                                          ║
# ║ Stage 1 (build): Has Maven, JDK, source code (~500MB)                   ║
# ║ Stage 2 (run):   Has only JRE and JAR file (~200MB)                     ║
# ║                                                                          ║
# ║ The final image only contains Stage 2 - much smaller to deploy!         ║
# ╚══════════════════════════════════════════════════════════════════════════╝

# ─────────────────────────────────────────────────────────────────────
# STAGE 1: BUILD
# ─────────────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory inside container
WORKDIR /app

# Copy Maven project files first (for better caching)
# Docker caches layers - if pom.xml doesn't change, dependencies won't re-download
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw.cmd .

# Download dependencies (cached if pom.xml unchanged)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application (skip tests for faster builds)
RUN mvn package -DskipTests

# ─────────────────────────────────────────────────────────────────────
# STAGE 2: RUN
# ─────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

# Add labels for documentation
LABEL maintainer="BetterMe Team"
LABEL description="BetterMe API - Self Improvement Application"

WORKDIR /app

# Create non-root user for security
# Running as root in containers is a security risk!
RUN addgroup -S betterme && adduser -S betterme -G betterme

# Copy ONLY the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R betterme:betterme /app

# Switch to non-root user
USER betterme

# Document the port (doesn't actually expose it, just documentation)
EXPOSE 8080

# Health check - container orchestrators use this
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/auth/health || exit 1

# Run the application
# Using exec form for proper signal handling
ENTRYPOINT ["java", "-jar", "app.jar"]
