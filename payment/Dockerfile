FROM openjdk:21-jdk-slim

COPY build/libs/*.jar app.jar

ENV ACTIVE_PROFILES=local

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]