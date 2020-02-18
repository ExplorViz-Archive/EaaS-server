package net.explorviz.eaas.service.docker;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.Validate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Scanner;

/**
 * Represents a docker-compose service definition, i.e. a docker-compose file definition ({@code -f}) and a project name
 * ({@code -p}).
 */
@Getter
@ToString
public abstract class DockerComposeDefinition {
    private static final String COMPOSE_FILE_PREFIX = "docker-compose/";
    private static final String COMPOSE_FILE_SUFFIX = ".yml";

    @NonNull
    private final String name;

    @Setter(AccessLevel.PACKAGE)
    private boolean running;

    @Nullable
    @Setter(AccessLevel.PACKAGE)
    private ZonedDateTime startedTime;

    /**
     * @param name Project name for docker-compose, unique for the build attached
     */
    protected DockerComposeDefinition(@NonNull String name) {
        Validate.notBlank(name, "name may not be empty");

        this.name = name;
    }

    @NonNull
    public abstract String getComposeDefinition();

    /**
     * Read the compose definition file with the given name (without extension) from the resources and return it as a
     * string.
     */
    @NonNull
    protected static String readComposeFile(@NonNull String filename) {
        try (Scanner scanner = new Scanner(
            new ClassPathResource(COMPOSE_FILE_PREFIX + filename + COMPOSE_FILE_SUFFIX).getInputStream(),
            StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Required file " + filename + " is not in class path. This is a bug.", e);
        }
    }
}
