# ExplorViz as a Service

Visualize your build artifacts with ExplorViz whenever you need.

## Build as docker image (recommended)

When building the docker image for EaaS, the entire build is done within a container and no build tools need to be installed on your system other than Docker.

Simply run the following command to create the image ready for production use:

```
$ docker build -t eaas-server:latest -f docker/Dockerfile .
```

Because the entire build runs inside the container, dependencies might have to be downloaded on every run. This can be very time consuming during development. To prevent this, another Dockerfile is included that uses the experimental cache mount feature to permanently store downloaded dependencies. To use this file your docker daemon needs to run in experimental mode, then build the image like this:

```
$ DOCKER_BUILDKIT=1 docker build -t eaas-server:latest -f docker/experimental.Dockerfile .
```

### Running the docker image

If you have docker-compose installed you can use the easy-to-configure `docker-compose.yml` in the docker directory. You can configure some options from there. Start the container by running:

```
$ docker-compose -f docker/docker-compose.yml up -d
```

If you prefer to use `docker` directly, use something like

```
$ docker run -d -v /var/opt/eaas/ -v /var/run/docker.sock:/var/run/docker.sock -p 8080:8080 eaas-server:latest
```

You can find additional options by reading the docker-compose file.

## Build as executable jar

To build EaaS as a jar file, all you need on your system is a Java Development Kit (JDK) version 11 or newer.

Simply run the following command to do a clean build, including running static analysis tools:

```
$ ./mvnw
```

This will download a supported version of the Maven build tool automatically. If you have a local Maven installation that you want to use, replace `./mvnw` with `mvn`. (Use **Maven 3.5** or newer)

During the build, a supported NodeJS version is also downloaded and used to build the frontend. You can prevent that and use a locally installed version of NodeJS and npm by adding the parameter `-P system-node`. (Use **NodeJS v12.15** or newer)

To create a production build, add `-P production`. The runnable jar file is created as `target/explorviz-as-a-service-<version>.jar` and can be run using `java -Dvaadin.productionMode -jar <path-to-jar>`.

(Note: If you need to specify multiple profiles with `-P`, separate them with a comma like this: `-P system-node,production`.)

### Development

During development, you might want to do incremental builds instead of clean builds every time; in this case specify the `package` goal: `./mvnw package` (this overrides the default goal of `clean verify`).

`Spring Boot DevTools` is included to support LiveReload integration with your IDE. Please check the documentation of your IDE on how to make use of this. Also check out the *Internal options* section at the bottom of `src/main/resources/application.properties` for a number of options that can help you when developing.

### Troubleshooting

If you get the following error message

> [ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.1:compile (default-compile) on project explorviz-as-a-service: Fatal error compiling: invalid flag: --release -> [Help 1]

it means that you tried to compile EaaS with a JDK version 8 or older. Make `java` and `javac` in your `PATH` point to a JDK 11. How to do this depends on your operating systems. Alternatively, specify the `JAVA_HOME` environment variable when running maven, like this:

```
$ JAVA_HOME=/usr/lib/jvm/java-11-openjdk ./mvnw
```

(Actual paths depend on your operating system, look at `ls /usr/lib/jvm`.)
