#syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /opt/app
COPY target/*.jar ./car-models-serivce.jar
ENTRYPOINT [ "sh","-c","java -jar car-models-serivce.jar" ]
