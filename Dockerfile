FROM ghcr.io/navikt/fp-baseimages/java:17
LABEL org.opencontainers.image.source=https://github.com/navikt/fp-infotrygd
ENV TZ=Europe/Oslo

ENV JAVA_OPTS="${JAVA_OPTS} -Xms270M"

COPY target/*.jar app.jar
EXPOSE 8080