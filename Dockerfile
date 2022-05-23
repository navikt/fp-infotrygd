FROM navikt/java:17

ENV JAVA_OPTS="${JAVA_OPTS} -Xms270M"

COPY target/*.jar app.jar
EXPOSE 8080

