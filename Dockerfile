FROM openjdk:24
WORKDIR /gizmo
COPY target/gizmo.jar gizmo.jar
EXPOSE 8080
CMD ["java", "-jar", "gizmo.jar"]