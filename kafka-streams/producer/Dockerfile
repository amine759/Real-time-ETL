# Stage 1: Build the assembly JAR using sbt
FROM hseeberger/scala-sbt:11.0.5_1.3.6_2.13.1 as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the root build files

RUN ls ../
RUN ls  ../../

COPY ../build.sbt /app/
COPY ../project /app/project/
COPY ../../archive /app/archive/


# Copy the producer module
COPY producer /app/producer/

# Navigate to the producer project and build the assembly JAR
RUN sbt "project producer" clean assembly
RUN ls -l /app/target/
# Stage 2: Create the runtime image
FROM openjdk:11-jre-slim

# Metadata
LABEL maintainer="amine@rasta.com"
LABEL description="Producer Service for Kafka Streams ETL"

# Create a non-root user and group
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Set the working directory
WORKDIR /app

# Copy the assembly JAR from the builder stage
COPY --from=builder /app/producer/target/scala-2.12/producer-service-assembly-0.1.0-SNAPSHOT.jar app.jar

# Change ownership to the non-root user
RUN chown appuser:appgroup app.jar

# Switch to the non-root user
USER appuser

# Expose the application port
EXPOSE 8081

# Define the entrypoint to run the service
ENTRYPOINT ["java", "-jar", "app.jar"]
