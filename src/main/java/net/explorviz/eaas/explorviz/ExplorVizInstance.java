package net.explorviz.eaas.explorviz;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Represents a single instance of ExplorViz that can be accessed on a dedicated port.
 */
@ToString
@Getter
public class ExplorVizInstance {
    private static final String COMPOSE_FILE_PREFIX = "docker-compose/explorviz-";
    private static final String COMPOSE_FILE_SUFFIX = ".yml.tpl";

    @ToString.Exclude
    @Getter(AccessLevel.PACKAGE)
    private final int id;
    @ToString.Exclude
    @Getter(AccessLevel.PACKAGE)
    private final String composeDefinition;

    private final long buildId;
    private final String version;
    private final String name;
    private final int frontendPort;
    private final String accessURL;
    private final String image;

    private final Instant createdTime;

    /**
     * @param id      An ID for this instance, unique only while this instance is running.
     *                Used by the {@link ExplorVizManager} to keep track of running instances.
     * @param version Version of ExplorViz' docker-compose file to use. Expect issues with the <pre>dev</pre>
     *                version, as it refers to images unknown at the time of building EaaS and the docker-compose
     *                file we ship might have become incompatible with the current dev images.
     * @param name    Project name for docker-compose, unique for the build attached
     * @param image   Docker image tag or ID of the application we want to visualize
     */
    ExplorVizInstance(int id, long buildId, @NonNull String version, String name, int frontendPort,
                      @NonNull String accessURL, @NonNull String image) {
        this.id = id;
        this.buildId = buildId;
        this.version = version;
        this.name = name;
        this.frontendPort = frontendPort;
        this.accessURL = accessURL;
        this.image = image;

        String composeTemplate = readResourceFile(COMPOSE_FILE_PREFIX + version + COMPOSE_FILE_SUFFIX);
        this.composeDefinition = composeTemplate
            .replace("%EXPLORVIZ_VERSION%", version)
            .replace("%INSTANCE_ID%", Integer.toString(id))
            .replace("%INSTANCE_NAME%", name)
            .replace("%FRONTEND_PORT%", Integer.toString(frontendPort))
            .replace("%ACCESS_URL%", accessURL)
            .replace("%BUILD_IMAGE%", image);

        this.createdTime = Instant.now();
    }

    private static String readResourceFile(String filename) {
        try {
            return IOUtils.toString(new ClassPathResource(filename).getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Required file " + filename + " is not in class path. This is a bug.", e);
        }
    }
}
