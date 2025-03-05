# Use a base image with Java (adjust version as needed)
FROM amazoncorretto:17-alpine-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from your build directory
COPY target/cicd-worker-1.0-SNAPSHOT.jar /app/cicd-worker.jar

# Expose the port your Spring Boot app runs on (typically 8080)
EXPOSE 8080

# Command to run your worker
CMD ["java", "-jar", "cicd-worker.jar"]
