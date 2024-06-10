#syntax=docker/dockerfile:1

FROM maven:3.9.6-eclipse-temurin-17-alpine AS development
WORKDIR /opt/app
COPY target/*.jar ./car-models-service.jar
ENTRYPOINT [ "sh","-c","java -jar car-models-service.jar --spring.profiles.active=dev" ]