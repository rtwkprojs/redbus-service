FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the pre-built JAR file
COPY target/*.jar app.jar

# Expose the service port
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
