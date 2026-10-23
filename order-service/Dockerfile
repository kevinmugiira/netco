FROM eclipse-temurin:21-jre
WORKDIR /app
COPY order-service/target/order-service-1.0-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
