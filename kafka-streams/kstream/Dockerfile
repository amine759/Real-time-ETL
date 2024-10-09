# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Metadata
LABEL maintainer="amine@rasta.com"
LABEL description="kStream Processor Service for Kafka Streams ETL"

# Create a non-root user and group
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Set the working directory
WORKDIR /app

# Copy the fat JAR into the container
COPY target/scala-2.12/kstream-service-assembly-0.1.0-SNAPSHOT.jar kstream-service.jar

# Change ownership to the non-root user
RUN chown -R appuser:appgroup /app

# Switch to the non-root user
USER appuser

# Expose the application port (if applicable)
EXPOSE 8082

# Define the entrypoint to run the JAR
ENTRYPOINT ["java", "-jar", "kstream-service.jar"]
