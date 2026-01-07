# Use small Java 21 runtime
FROM eclipse-temurin:21-jre-alpine

# Set working directory inside container
WORKDIR /app

# Copy the built Spring Boot jar into the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose default Spring Boot port
EXPOSE 8888

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
