# Stage 1: Build
FROM gradle:7.5.1-jdk11 AS builder
WORKDIR /app
COPY . ./
RUN gradle build --no-daemon

# Stage 2: Run
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]