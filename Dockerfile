# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app

# Cache dependencies to speed up subsequent builds
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the package
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jre-slim
WORKDIR /app

# Use the exact build artifact name if possible, or use a specific argument
# to avoid copying "plain" or "sources" jars.
COPY --from=builder /app/target/url-shortener-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]