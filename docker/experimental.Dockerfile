# syntax=docker/dockerfile:experimental

###############################################################################
# Build environment
###############################################################################
# Try to switch to alpine-slim once https://github.com/AdoptOpenJDK/openjdk-docker/issues/103 is fixed
FROM adoptopenjdk/openjdk11:alpine AS builder

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
RUN --mount=type=cache,uid=1000,gid=1000,target=/home/build/.m2/ --mount=type=cache,uid=1000,gid=1000,target=/home/build/eaas/node_modules/ \
    ./mvnw -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn \
    -P system-node,production \
    dependency:go-offline

COPY src src
COPY package.json .
COPY package-lock.json .
COPY webpack.config.js .

# Specifically use goal package here to avoid running static analysis tools
RUN --mount=type=cache,uid=1000,gid=1000,target=/home/build/.m2/ --mount=type=cache,uid=1000,gid=1000,target=/home/build/eaas/node_modules/ \
    ./mvnw -B \
    -P system-node,production \
    package -DskipTests

###############################################################################
# Runtime environment
###############################################################################
FROM adoptopenjdk/openjdk11:alpine-jre

RUN apk --no-cache add \
    su-exec docker-compose \
 && adduser -D -h /var/opt/eaas eaas

COPY docker/entrypoint.sh /
# Adjust this argument for new versions
ARG JAR_FILE=explorviz-as-a-service-1.1-SNAPSHOT.jar
# TODO: Use unpacked jar for faster startup
COPY --from=builder --chown=root:root /home/build/eaas/target/${JAR_FILE} /opt/eaas/explorviz-as-a-service.jar

EXPOSE 8080
VOLUME /var/opt/eaas/

WORKDIR /opt/eaas/
ENTRYPOINT ["/entrypoint.sh", "java", "-Dvaadin.productionMode", "-jar", "explorviz-as-a-service.jar"]
