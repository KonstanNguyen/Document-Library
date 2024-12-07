# Use Maven to build the application
FROM maven:3.8.7 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies first to take advantage of Docker cache
#COPY pom.xml .
#RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY . .

# Build the application (this will create the target directory and JAR file)
RUN mvn clean package -DskipTests

# Use a smaller image to run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
