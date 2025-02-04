# Use the official Maven image to build the project
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file to download dependencies
COPY pom.xml .

# Download Maven dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy the entire project into the container
COPY . .

# Build the project and create a JAR file
RUN mvn clean package -DskipTests

# Use a minimal OpenJDK runtime image to run the application
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/PetStore-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "app.jar"]
