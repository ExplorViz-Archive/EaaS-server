package net.explorviz.eaas.service.explorviz;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.explorviz.eaas.model.entity.Build;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Scanner;

/**
 * Represents a single instance of ExplorViz that can be accessed on a dedicated port, running to visualize a single
 * build image.
 * <p>
 * This object should not be kept around after the instance has been stopped.
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
    @ToString.Exclude
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private boolean running;

    private final Build build;
    private final String version;
    private final String name;
    private final int frontendPort;
    private final String accessURL;

    private final ZonedDateTime startedTime;

    /**
     * @param id           An ID for this instance, unique only while this instance is running. Used by the {@link
     *                     ExplorVizManager} to keep track of running instances
     * @param build        The {@link Build} this instance is visualizing
     * @param version      Version of ExplorViz' docker-compose file to use. Expect issues with the {@code dev} version,
     *                     as it refers to images unknown at the time of building EaaS and the docker-compose file we
     *                     ship might have become incompatible with the current dev images
     * @param name         Project name for docker-compose, unique for the build attached
     * @param frontendPort Port number this instance will be exposed on
     * @param accessURL    URL for end-user to access this instance on
     */
    ExplorVizInstance(int id, @NonNull Build build, @NonNull String version, @NonNull String name, int frontendPort,
                      @NonNull String accessURL) {
        this.id = id;
        this.build = build;
        this.version = version;
        this.name = name;
        this.frontendPort = frontendPort;
        this.accessURL = accessURL;

        String composeTemplate = readResourceFile(COMPOSE_FILE_PREFIX + version + COMPOSE_FILE_SUFFIX);
        this.composeDefinition = composeTemplate
            .replace("%EXPLORVIZ_VERSION%", version)
            .replace("%INSTANCE_ID%", Integer.toString(id))
            .replace("%INSTANCE_NAME%", name)
            .replace("%FRONTEND_PORT%", Integer.toString(frontendPort))
            .replace("%ACCESS_URL%", accessURL)
            .replace("%APPLICATION_IMAGE%", build.getDockerImage());

        this.startedTime = ZonedDateTime.now();
    }

    private static String readResourceFile(@NonNull String filename) {
        try (Scanner scanner = new Scanner(new ClassPathResource(filename).getInputStream(), StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Required file " + filename + " is not in class path. This is a bug.", e);
        }
    }
}
