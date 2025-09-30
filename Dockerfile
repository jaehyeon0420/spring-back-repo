FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} spring-docker-image.jar

ENTRYPOINT ["java","-jar","/spring-docker-image.jar"]