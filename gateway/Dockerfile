FROM amazoncorretto:11
#ENV TZ="Europe/Moscow"
COPY target/shareit-gateway-0.0.1-SNAPSHOT.jar gateway.jar
ENTRYPOINT ["java", "-jar", "/gateway.jar"]
