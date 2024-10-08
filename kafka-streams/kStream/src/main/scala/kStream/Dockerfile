# Dockerfile for Kafka Streams
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Copy the application JAR
COPY target/your-kstream-jar.jar kstream.jar

# Set the entry point
ENTRYPOINT ["java", "-jar", "kstream.jar"]
