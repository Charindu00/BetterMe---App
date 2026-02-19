# Multi-stage Dockerfile for BetterMe API

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw.cmd .

RUN mvn dependency:go-offline -B

COPY src src

RUN mvn package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="BetterMe Team"
LABEL description="BetterMe API - Self Improvement Application"

WORKDIR /app

RUN addgroup -S betterme && adduser -S betterme -G betterme

COPY --from=build /app/target/*.jar app.jar

RUN chown -R betterme:betterme /app

USER betterme

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/auth/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
