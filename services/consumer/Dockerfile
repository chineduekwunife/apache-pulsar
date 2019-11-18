FROM openjdk:8u111-jdk-alpine

VOLUME /tmp

ADD build/libs/consumer-*.jar app.jar

ENV JAVA_TOOL_OPTIONS=""

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
