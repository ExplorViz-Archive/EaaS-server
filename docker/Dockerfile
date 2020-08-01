###############################################################################
# Build environment
###############################################################################
FROM adoptopenjdk/openjdk11-openj9:alpine-slim AS builder

RUN apk --no-cache add \
    nodejs npm \
 && addgroup -g 1000 build \
 && adduser -D -u 1000 -G build build \
 && mkdir /home/build/eaas \
 && chown build:build /home/build/eaas

USER build:build
WORKDIR /home/build/eaas

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies before copying source files for best layer caching
RUN \
    ./mvnw -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn \
    -P production \
    dependency:go-offline

COPY webpack.config.js .
COPY package.json .
COPY package-lock.json .
COPY src src

# Specifically use goal package here to avoid running static analysis tools
RUN \
    ./mvnw -B \
    -P production \
    package \
 && mkdir extract \
 && (cd extract; jar -xf ../target/explorviz-as-a-service-*.jar)

###############################################################################
# Runtime environment
###############################################################################
FROM adoptopenjdk/openjdk11-openj9:alpine-jre

RUN apk --no-cache add \
    su-exec docker-compose \
 && adduser -D -h /var/opt/eaas eaas

COPY docker/entrypoint.sh /
COPY --from=builder --chown=root:root /home/build/eaas/extract/BOOT-INF/lib /opt/eaas/lib
COPY --from=builder --chown=root:root /home/build/eaas/extract/META-INF /opt/eaas/META-INF
COPY --from=builder --chown=root:root /home/build/eaas/extract/BOOT-INF/classes /opt/eaas

EXPOSE 8080
VOLUME /var/opt/eaas/

WORKDIR /opt/eaas/
ENTRYPOINT ["/entrypoint.sh"]
