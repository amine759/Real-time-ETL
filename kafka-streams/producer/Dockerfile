# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Metadata
LABEL maintainer="amine@rasta.com"
LABEL description="Producer Service for Kafka Streams ETL"

# Create a non-root user and group
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Set the working directory
WORKDIR /app

# Copy the fat JAR into the container
COPY target/scala-2.12/producer-service-assembly-0.1.0-SNAPSHOT.jar producer-service.jar

# Change ownership to the non-root user
RUN chown -R appuser:appgroup /app

# Switch to the non-root user
USER appuser

# Expose the application port (if applicable)
EXPOSE 8081

# Define the entrypoint to run the JAR
ENTRYPOINT ["java", "-jar", "producer-service.jar"]
