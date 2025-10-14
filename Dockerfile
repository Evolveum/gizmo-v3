FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build

COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -DskipTests dependency:go-offline

COPY . .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -DskipTests package

FROM eclipse-temurin:25 AS runtime
WORKDIR /opt/app

RUN mkdir -p /opt/app/logs
ENV LOG_DIR=/opt/app/logs

ENV JAVA_OPTS=""

COPY --from=build /build/target/gizmo.jar /opt/app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /opt/app/app.jar"]
