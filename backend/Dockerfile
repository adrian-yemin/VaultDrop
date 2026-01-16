FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/backend-0.0.1-SNAPSHOT.jar backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "backend.jar"]
