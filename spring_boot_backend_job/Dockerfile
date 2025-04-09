# Dockerfile

# -----------------------
# Stage 1: Build the JAR
# -----------------------
FROM maven:3.8.7-openjdk-11 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# -----------------------
# Stage 2: Minimal runtime image
# -----------------------
FROM openjdk:11-jre-slim
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=build /app/target/spring_boot_backend_template-0.0.1.jar app.jar

# Expose the port your app runs on
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
