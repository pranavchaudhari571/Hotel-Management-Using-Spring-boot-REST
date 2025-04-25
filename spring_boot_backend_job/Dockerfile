# Step 1: Build with Maven
FROM maven:3.8.5-openjdk-11-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run with JRE
FROM openjdk:11-jdk-slim
WORKDIR /app
COPY --from=build /app/target/spring_boot_backend_template-0.0.1.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
