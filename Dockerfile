FROM ghcr.io/navikt/fp-baseimages/java:21
LABEL org.opencontainers.image.source=https://github.com/navikt/fp-infotrygd

ENV JAVA_OPTS="${JAVA_OPTS} -Xms270M"

COPY target/*.jar app.jar