# ExplorViz as a Service

Collect build artifacts of your Java application to visualize them in ExplorViz whenever you need.

## What is this?

> [ExplorViz](https://www.explorviz.net/) is an open source research monitoring and visualization approach, which uses dynamic analysis techniques to provide a live trace visualization of large software landscapes.

[ExplorViz as a Service](https://github.com/ExplorViz/EaaS-server) (EaaS) allows you to collect build artifacts and run them in ExplorViz instances on-demand.

When submitting builds to EaaS, they need to be wrapped inside a docker image that runs both the application and, if necessary, some load to create more interesting visualizations. The application needs to be run with the Kieker monitoring tool to obtain records and send them to ExplorViz. The build image will be run together with ExplorViz inside a docker-compose stack, i.e. they run on the same network. The endpoint to submit Kieker records to is `analysis-service` on port `10133` (SingleSocketTcpWriter).
If you use the [EaaS-base-image](https://github.com/ExplorViz/EaaS-base-image) for your build images, then you do not need to worry about configuring Kieker yourself.
The server provides a simple HTTP API to submit builds to; a shell script to build and upload images can be found in `submission/submit-eaas.sh`. You can copy this file into your repository or keep it somewhere on your CI server to run it during a build.

See [EaaS-demo-application](https://github.com/ExplorViz/EaaS-demo-application) for a full example making use of this software.

## Requirements

The EaaS server must be able to create and run docker containers. By default it is expected that the server can access `/var/run/docker.sock`. This socket is also mounted inside the container when EaaS itself runs in Docker (as recommended). However, you can configure another Docker API endpoint through environment variables. Currently there is no support to make use of Docker Swarm or multiple Docker daemons.

You will need a fairly powerful machine. We recommend 512 MiB RAM for EaaS itself. EaaS allows you to run multiple visualizations at the same time. We recommend at least 1.5 cores and 2 GiB RAM on the server per running instance for ExplorViz itself when visualizing small applications. This does not include the resources used by the application in question and might not suffice for visualizing bigger applications.

## Build as docker image (recommended)

When building the docker image for EaaS, the entire build is done within a container and no build tools need to be installed on your system other than Docker.

Simply run the following command to create the image ready for production use:

```
$ docker build -t explorviz/eaas-server:latest .
```

Because the entire build runs inside the container, dependencies might have to be downloaded on every run. This can be very time consuming during development. To prevent this, another Dockerfile is included that uses the experimental cache mount feature to permanently store downloaded dependencies. To use this file your docker daemon needs to run in experimental mode, then build the image like this:

```
$ DOCKER_BUILDKIT=1 docker build -t explorviz/eaas-server:latest -f docker/experimental.Dockerfile .
```

### Running the docker image

If you have docker-compose installed you can use the easy-to-configure `docker-compose.yml`. You can configure some options from there. Start the container by running:

```
$ docker-compose up -d
```

If you prefer to use `docker` directly, use something like

```
$ docker run -d -v eaas-database:/var/opt/eaas/ -v /var/run/docker.sock:/var/run/docker.sock:ro -p 8080:8080 explorviz/eaas-server:latest
```

You can find additional options by reading the docker-compose file.

Then, open `http://localhost:8080` (or the address wherever this server is running) in your browser. Default administrator credentials are `admin`:`password`.

## Build as executable jar

To build EaaS as a jar file, all you need on your system is a Java Development Kit (JDK) version 11 or newer.

Simply run the following command to do a clean build, including running static analysis tools:

```
$ ./mvnw
```

This will download a supported version of the Maven build tool automatically. If you have a local Maven installation that you want to use, replace `./mvnw` with `mvn` (Use **Maven 3.5** or newer). During the build, if not available on your system, a supported NodeJS version is also downloaded and used to build the frontend.

To create a production build, add `-P production`. The runnable jar file is created as `target/explorviz-as-a-service-<version>.jar` and can be run using `java -Dvaadin.productionMode -jar <path-to-jar>`. Be aware that you need to have a docker daemon running and the user you are running the jar as must have access to `/var/run/docker.sock`, and you also need to have `docker-compose` in your `PATH`, or else the server won't be able to start.

### Development

During development, you might want to do incremental builds instead of clean builds every time; in this case specify the `verify` goal: `./mvnw verify` (this overrides the default goal of `clean verify`), or the `package` goal to skip static analyis tools as well.

You can also run the server directly without packaging by running `./mvnw spring-boot:run`. *Spring Boot DevTools* is included to support LiveReload so changes will apply automatically while the server is running. Please check the documentation of your IDE on how to make use of this.

Also check out the *Internal options* section at the bottom of `src/main/resources/application.properties` for a number of options that can help you when developing. `eaas.docker.useDummyImplementation=true` will allow you to run the server without needing access to a docker daemon.

## Troubleshooting

#### org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'projectsController' ... java.net.SocketException: No such file or directory / java.io.IOException: native connect() failed : Permission denied

The docker socket file `/var/run/docker.sock` (or the endpoint specified in the `DOCKER_HOST` variable, e.g in `docker-compose.yml` if you use that) does not exist or you have insufficient permission to access it. Make sure you have Docker installer on the system you try to run the EaaS server on and that you have permission to access it. If EaaS is running in a docker container itself make sure the socket is passed through to the container correctly.

During development you can use `--eaas.docker.useDummyImplementation=true` to run EaaS without docker.

#### java.io.IOException: Cannot run program "docker-compose": error=2, No such file or directory

You are running from the jar and the `docker-compose` tool is not in your `PATH`. Please edit your operating systems `PATH` variable to include the directory that `docker-compose` is in. Docker Compose is required to run ExplorViz visualizations. During development this can be disabled with the dummy setting above as well.

#### The requested URL returned error: 413 (submit-eaas.sh)

If you run the EaaS-server behind a reverse proxy (e.g. to do TLS termination) then your reverse proxy might limit the maximum request body size. Check the documentation of your reverse proxy on how to modify this limit. A limit of 1024 MB should be sufficient for most applications.

Additionally, the EaaS-server has a built-in limit of 1024 MB. If your build images are bigger than that, then you should take a look into optimizing your image, as storage requirements can become expensive with images this large. You can raise the limit by adding

```
- "--spring.servlet.multipart.max-file-size=xxxxMB"
- "--spring.servlet.multipart.max-request-size=xxxxMB"
```

to the `command` in your `docker-compose.yml`. Replace `xxxx` with the new limit.
