FROM openjdk:8-jre-alpine
COPY backend*.jar           /jars/backend.jar
COPY application.yml        /jars/application.yml

EXPOSE 8080
CMD ["java", "-jar", "/jars/backend.jar", "--spring.config.location=file:/jars/application.yml"]