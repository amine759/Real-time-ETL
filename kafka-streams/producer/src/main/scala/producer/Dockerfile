# Dockerfile for Kafka Producer
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Copy the application JAR
COPY target/your-producer-jar.jar producer.jar

# Set the entry point
ENTRYPOINT ["java", "-jar", "producer.jar"]
