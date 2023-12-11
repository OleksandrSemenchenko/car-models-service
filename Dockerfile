#syntax=docker/dockerfile:1

FROM maven:3.9.5-eclipse-temurin-17-alpine as build
WORKDIR /usr/src/car-models-service
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=bind,source=pom.xml,target=./pom.xml \
    --mount=type=bind,source=car-models-service-core/pom.xml,target=car-models-service-core/pom.xml \
    cd car-models-service-core; mvn dependency:resolve
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=bind,source=pom.xml,target=./pom.xml \
    --mount=type=bind,source=car-models-service-core/pom.xml,target=car-models-service-core/pom.xml \
    --mount=type=bind,source=car-models-service-core/src,target=car-models-service-core/src/ \
    cd car-models-service-core && mvn install && \
    mkdir -p target/dependency && cd target/dependency && jar -xf ../*.jar
    
FROM eclipse-temurin:17-jdk-alpine as development
WORKDIR /opt/app
ARG DEPENDENCY=/usr/src/car-models-service/car-models-service-core/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib ./lib/
COPY --from=build ${DEPENDENCY}/META-INF ./META-INF/
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes ./
ENTRYPOINT [ "sh","-c","java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:8000 \
    -cp /opt/app:/opt/app/lib/* ua.com.foxminded.CarModelsService" ]
