# First stage: Build the application using Maven and OpenJDK 21
FROM maven:3.8.5-openjdk-21 AS build

# Set working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml ./
COPY src ./src

# Install dependencies and build the application
RUN mvn clean package -DskipTests

# Second stage: Run the application using a lightweight OpenJDK 21 image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8060

# Run the application
CMD ["java", "-jar", "app.jar"]
