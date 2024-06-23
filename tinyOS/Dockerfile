# Use the official Gradle image to build the app
FROM gradle:7.4.2-jdk11 AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon

# Use the official OpenJDK image to run the app
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/build/libs/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]