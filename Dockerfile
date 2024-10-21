# Use the official Maven image for building the application
FROM maven:3.8.1-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and source files
COPY pom.xml .
COPY src ./src

# Build the application and skip tests
RUN mvn clean package -DskipTests

# Use a smaller OpenJDK image for the final image
FROM openjdk:17.0.1-jdk-slim

# Copy the JAR file from the build stage
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar demo.jar

# Expose the port that the application will run on
EXPOSE 8080

# Set the entry point for the application
ENTRYPOINT ["java", "-jar", "demo.jar"]