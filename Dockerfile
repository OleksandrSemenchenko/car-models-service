#syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-alpine as base
WORKDIR /usr/src/car-models-service
#RUN apk add maven
ADD https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip /opt/
RUN cd /opt && \
    unzip apache-maven-3.9.6-bin.zip &&\
    ln -s /opt/apache-maven-3.9.6/bin/mvn /bin/mvn
COPY pom.xml ./
COPY car-models-service-core/pom.xml ./car-models-service-core/
RUN --mount=type=cache,target=/root/.m2 \
    cd ./car-models-service-core && \
    mvn dependency:resolve

FROM base as build
COPY car-models-service-core/src/ ./car-models-service-core/src/
RUN --mount=type=cache,target=/root/.m2 \
    cd car-models-service-core && \ 
    mvn install && \
    mkdir -p target/dependency && \
    cd target/dependency && \
    jar -xf ../*.jar

FROM build as component-tests
COPY car-models-service-component-tests/pom.xml ./car-models-service-component-tests/
COPY car-models-service-component-tests/src/ ./car-models-service-component-tests/src/
RUN cd ./car-models-service-component-tests && \
    mvn test
    
FROM eclipse-temurin:17-jdk-alpine as development
WORKDIR /opt/car-models-service-core
#ARG JAVA_FILE=car-models-service-core/target/*.jar
#COPY --from=base /usr/src/car-models-service/${JAVA_FILE} ./car-models-service-core.jar
ARG DEPENDENCY=/usr/src/car-models-service/car-models-service-core/target/dependency
ARG JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000
ARG JAVA_CLASSPATH=/opt/car-models-service-core:/opt/car-models-service-core/lib
ARG MAIN_CLASS=ua.com.foxmindes.CarModelsService
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib ./lib
COPY --from=build ${DEPENDENCY}/META-INF ./META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes ./classes
ENTRYPOINT [ "sh", "-c", "java -cp ${JAVA_CLASSPATH} ${JAVA_OPTS} ${MAIN_CLASS}"]

FROM eclipse-temurin:17-jre-alpine as production
WORKDIR /opt/car-models-service-core
ARG JAVA_CLASSPATH=/opt/car-models-service-core:/opt/car-models-service-core/lib
ARG MAIN_CLASS=ua.com.foxmindes.CarModelsService
RUN addgroup -S demo && adduser -S demo -G demo
USER demo
#ARG JAVA_FILE=target/*.jar
#COPY --from=base /usr/src/car-models-service/${JAVA_FILE} ./car-models-service-core.jar
#ENTRYPOINT [ "sh", "-c", "java -jar ./car-models-service-core.jar" ]
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib ./lib
COPY --from=build ${DEPENDENCY}/META-INF ./META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes ./classes
ENTRYPOINT [ "sh", "-c", "java -cp ${MAIN_CLASS} ${MAIN_CLASS}" ]
