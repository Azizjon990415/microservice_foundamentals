# Build stage
FROM gradle:jdk21-alpine as build
WORKDIR /app

# Copy the Gradle wrapper and build configuration files
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle ./
COPY settings.gradle ./

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy the source code and build the application
COPY src ./src
RUN ./gradlew assemble --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8089

# Run the application
CMD ["java", "-jar", "app.jar"]