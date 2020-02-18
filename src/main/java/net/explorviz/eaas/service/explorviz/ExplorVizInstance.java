package net.explorviz.eaas.service.explorviz;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.service.docker.DockerComposeDefinition;
import org.springframework.lang.NonNull;

/**
 * Represents a single instance of ExplorViz that can be accessed on a dedicated port, running to visualize a single
 * build image.
 * <p>
 * This object should not be kept around after the instance has been stopped.
 */
@ToString(callSuper = true)
@Getter
public class ExplorVizInstance extends DockerComposeDefinition {
    private static final String EXPLORVIZ_PREFIX = "explorviz-";

    /**
     * This is the service you want to reads logs of when showing logs for a currently running build.
     *
     * @see net.explorviz.eaas.service.docker.DockerComposeAdapter#logs(DockerComposeDefinition, String...)
     */
    public static final String APPLICATION_SERVICE_NAME = "application";

    @ToString.Exclude
    @Getter(AccessLevel.PACKAGE)
    private final int id;

    @ToString.Exclude
    private final String composeDefinition;

    private final Build build;
    private final String version;
    private final int frontendPort;
    private final String accessURL;

    /**
     * @param name         Project name for docker-compose, unique for the build attached
     * @param id           An ID for this instance, unique only while this instance is running. Used by the {@link
     *                     ExplorVizManager} to keep track of running instances
     * @param build        The {@link Build} this instance is visualizing
     * @param version      Version of ExplorViz' docker-compose file to use. Expect issues with the {@code dev} version,
     *                     as it refers to images unknown at the time of building EaaS and the docker-compose file we
     *                     ship might have become incompatible with the current dev images
     * @param frontendPort Port number this instance will be exposed on
     * @param accessURL    URL for end-user to access this instance on
     */
    ExplorVizInstance(@NonNull String name, int id, @NonNull Build build, @NonNull String version, int frontendPort,
                      @NonNull String accessURL) {
        super(name);

        this.id = id;
        this.build = build;
        this.version = version;
        this.frontendPort = frontendPort;
        this.accessURL = accessURL;

        String composeTemplate = DockerComposeDefinition.readComposeFile(EXPLORVIZ_PREFIX + version);
        this.composeDefinition = composeTemplate
            .replace("%EXPLORVIZ_VERSION%", version)
            .replace("%INSTANCE_ID%", Integer.toString(id))
            .replace("%INSTANCE_NAME%", name)
            .replace("%FRONTEND_PORT%", Integer.toString(frontendPort))
            .replace("%ACCESS_URL%", accessURL)
            .replace("%APPLICATION_IMAGE%", build.getDockerImage());
    }
}
