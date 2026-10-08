FROM eclipse-temurin:21-jdk

WORKDIR app
COPY target/VolunNearApp-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application.yml application-dev.yml

CMD ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]