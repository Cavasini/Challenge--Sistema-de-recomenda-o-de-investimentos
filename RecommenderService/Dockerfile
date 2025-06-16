FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY target/RecommenderService-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]