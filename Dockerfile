###############################################################################
# Build environment
###############################################################################
FROM adoptopenjdk/openjdk11:alpine AS builder

RUN addgroup -S build \
 && adduser -S -G build -h /opt/build -D build
USER build:build
WORKDIR /opt/build

# Do not copy . to avoid copying node and node_modules
COPY mvnw .
COPY .mvn .mvn
COPY src src
COPY frontend frontend
COPY pom.xml .
COPY package.json .
COPY package-lock.json .
COPY webpack.config.js .

# Specifically use goal package here to avoid running static analysis tools
RUN ./mvnw -B -P production package
# Experimental dockerfile feature: caching build dependencies
#RUN --mount=type=cache,target=/opt/build/.m2 ./mvnw -B -P production package -DskipTests

###############################################################################
# Runtime environment
###############################################################################
FROM adoptopenjdk/openjdk11:alpine-jre

# Adjust this argument for new versions
ARG JAR_FILE=explorviz-as-a-service-1.0-SNAPSHOT.jar
# TODO: Use unpacked jar for faster startup
COPY --from=builder /opt/build/target/${JAR_FILE} /opt/eaas/explorviz-as-a-service.jar

RUN addgroup -S eaas \
 && adduser -S -G eaas -h /var/opt/eaas -D eaas
USER eaas:eaas
WORKDIR /opt/eaas/

EXPOSE 8080
VOLUME /var/opt/eaas/

# TODO: Add -spring.config.location= and generate production-mode configuration from environment variables
CMD ["java", "-Dvaadin.productionMode", "-jar", "explorviz-as-a-service.jar"]
