package net.explorviz.eaas.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a {@link DockerAdapter} using the <a href="https://github.com/docker-java/docker-java>docker-java</a>
 * library, specifically using the {@link DockerClient}.
 * <p>
 * This library accepts configuration values through environment variables or java system properties.
 * We do not handle these ourselves; instead let the user care about it when launching this java application.
 */
class DockerJavaImplementation implements DockerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DockerJavaImplementation.class);

    private final DockerClient docker;

    DockerJavaImplementation() throws AdapterException {
        // Settings are read from environment variables as well as system properties
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        docker = DockerClientBuilder.getInstance(builder.build()).build();

        Info info = docker.infoCmd().exec();
        logger.info("Docker client initialized, server version {}", info.getServerVersion());
        logger.debug("Full docker information: {}", info);
    }
}
