FROM eclipse-temurin:21-jre
WORKDIR /app
COPY inventory-service/target/inventory-service-1.0-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
