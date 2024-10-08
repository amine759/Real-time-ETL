# Dockerfile for Kafka Consumer
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Copy the application JAR
COPY target/your-consumer-jar.jar consumer.jar

# Set the entry point
ENTRYPOINT ["java", "-jar", "consumer.jar"]
