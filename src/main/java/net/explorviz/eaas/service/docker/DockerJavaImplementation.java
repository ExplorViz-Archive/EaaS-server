package net.explorviz.eaas.service.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InfoCmd;
import com.github.dockerjava.api.command.LoadImageCmd;
import com.github.dockerjava.api.command.RemoveImageCmd;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implements a {@link DockerAdapter} using the <a href="https://github.com/docker-java/docker-java">docker-java</a>
 * library, specifically using the {@link DockerClient}.
 * <p>
 * This library uses the docker API specified through environment variables or java system properties (defaulting to
 * the local docker socket). We do not handle these ourselves; instead let the user care about it when launching this
 * java application.
 */
@Slf4j
class DockerJavaImplementation implements DockerAdapter {
    private final DockerClient docker;

    // TODO: We're not getting checked exceptions from docker-java. We should actually throw AdapterExceptions.

    DockerJavaImplementation() throws AdapterException {
        // Settings are read from environment variables (see docker/docker-compose.yml)
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        docker = DockerClientBuilder.getInstance(builder.build()).build();

        try (InfoCmd cmd = docker.infoCmd()) {
            Info info = cmd.exec();
            log.info("Docker client initialized, server version {}", info.getServerVersion());
            log.debug("Full docker information: {}", info);
        }
    }

    @Override
    public void loadImage(InputStream input) {
        log.info("Loading new image");

        try (LoadImageCmd cmd = docker.loadImageCmd(input)) {
            cmd.exec();
        }
    }

    @Override
    public void deleteImage(String image) {
        log.info("Deleting image '{}'", image);

        try (RemoveImageCmd cmd = docker.removeImageCmd(image)) {
            cmd.exec();
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            docker.close();
        } catch (IOException e) {
            log.error("Error closing docker-java api", e);
        }
    }
}
