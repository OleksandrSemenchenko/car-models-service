#syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-alpine as development
WORKDIR /deployment
COPY target/*.jar ./car-models-serivce.jar
ENTRYPOINT [ "sh","-c","java -jar \
  -Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:8000' \
  car-models-serivce.jar" ]
    