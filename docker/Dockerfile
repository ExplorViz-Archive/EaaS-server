###############################################################################
# Build environment
###############################################################################
FROM adoptopenjdk/openjdk11:alpine AS builder

RUN apk --no-cache add \
    nodejs npm \
 && adduser -D build \
 && mkdir /home/build/eaas \
 && chown build:build /home/build/eaas

USER build:build
WORKDIR /home/build/eaas

# Do not copy . to avoid copying node and node_modules
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies before copying source files for best layer caching
RUN ./mvnw -B -P system-node,production dependency:go-offline

COPY src src
COPY frontend frontend
COPY package.json .
COPY package-lock.json .
COPY webpack.config.js .

# Specifically use goal package here to avoid running static analysis tools
RUN ./mvnw -B -P system-node,production package

###############################################################################
# Runtime environment
###############################################################################
FROM adoptopenjdk/openjdk11:alpine-jre

RUN apk --no-cache add \
    su-exec docker-compose \
 && adduser -D -h /var/opt/eaas eaas

COPY docker/entrypoint.sh /
# Adjust this argument for new versions
ARG JAR_FILE=explorviz-as-a-service-1.0-SNAPSHOT.jar
# TODO: Use unpacked jar for faster startup
COPY --from=builder /home/build/eaas/target/${JAR_FILE} /opt/eaas/explorviz-as-a-service.jar

EXPOSE 8080
VOLUME /var/opt/eaas/

WORKDIR /opt/eaas/

ENTRYPOINT ["/entrypoint.sh"]
# TODO: Add -spring.config.location= and generate production-mode configuration from environment variables
CMD ["java", "-Dvaadin.productionMode", "-jar", "explorviz-as-a-service.jar"]
