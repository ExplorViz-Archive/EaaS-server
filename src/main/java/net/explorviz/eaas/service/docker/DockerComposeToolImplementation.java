package net.explorviz.eaas.service.docker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implements a {@link DockerComposeAdapter} by calling the <b>docker-compose</b> command line utility.
 * <p>
 * This utility expects the presence of environment variables for non-standard configurations, which we do not handle
 * ourselves. The user should pass these environment variables to our process when starting.
 */
@Slf4j
class DockerComposeToolImplementation implements DockerComposeAdapter {
    private static final String DOCKER_COMPOSE_COMMAND = "docker-compose";
    private static final int NORMAL_EXIT_CODE = 0;

    private final long operationTimeout;

    /**
     * @param operationTimeout Timeout in milliseconds for docker-compose calls.
     */
    DockerComposeToolImplementation(long operationTimeout) throws AdapterException {
        this.operationTimeout = operationTimeout;

        try {
            // TODO: Print version information
            runCommand(null, null, "version");
        } catch (IOException e) {
            throw new AdapterException(DOCKER_COMPOSE_COMMAND + " is not functional", e);
        }
    }

    /**
     * Execute a docker-compose command blockingly.
     *
     * @param projectName       Project name, passed in via <pre>-p name</pre> if not {@code null} (instead of using the
     *                          folder's name)
     * @param composeDefinition If not {@code null}, will be piped into the docker-compose command and
     *                          <pre>-f /dev/stdin</pre> will be added to the arguments.
     * @param args              The arguments to pass to docker-compose.
     * @throws AdapterException See {@link ProcessBuilder#start()}
     */
    protected void runCommand(@Nullable String projectName, @Nullable String composeDefinition,
                              String... args) throws IOException, AdapterException {
        List<String> command = new ArrayList<>(args.length + 5);
        command.add(DOCKER_COMPOSE_COMMAND);
        if (projectName != null) {
            command.add("-p");
            command.add(projectName);
        }
        if (composeDefinition != null) {
            command.add("-f");
            command.add("/dev/stdin");
        }
        Collections.addAll(command, args);

        ProcessBuilder builder = new ProcessBuilder(command);
        // TODO: Properly redirect into logger
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);

        log.info("Running command: {}", String.join(" ", command));
        Process process = builder.start();

        if (composeDefinition != null) {
            try (OutputStreamWriter stdin = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
                stdin.write(composeDefinition);
            }
        }

        try {
            if (!process.waitFor(operationTimeout, TimeUnit.MILLISECONDS)) {
                log.error("{} operation timed out, forcibly terminating process...", DOCKER_COMPOSE_COMMAND);
                process.destroyForcibly();
                throw new AdapterException("Operation timed out");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int exitCode = process.exitValue();
        if (exitCode != NORMAL_EXIT_CODE) {
            log.error("{} exited with error code {}. See log for more information", DOCKER_COMPOSE_COMMAND, exitCode);
            throw new AdapterException(DOCKER_COMPOSE_COMMAND + " command failed (error " + exitCode + ")");
        }
    }

    /**
     * Run a docker-compose command with a compose definition. This also needs a project name, since we don't have a
     * folder whose name docker-compoe could use.
     */
    protected void runComposeDefinition(@NonNull String name, @NonNull String composeDefinition, String... args)
        throws AdapterException {
        Validate.notBlank(name, "name may not be empty");
        Validate.notBlank(composeDefinition, "composeDefinition may not be empty");

        try {
            runCommand(name, composeDefinition, args);
        } catch (IOException e) {
            throw new AdapterException("Running " + DOCKER_COMPOSE_COMMAND + " failed", e);
        }
    }

    @Override
    public void up(@NonNull String name, @NonNull String composeDefinition) throws AdapterException {
        runComposeDefinition(name, composeDefinition, "up", "-d");
    }

    @Override
    public void down(@NonNull String name, @NonNull String composeDefinition) throws AdapterException {
        runComposeDefinition(name, composeDefinition, "down", "-v");
    }
}
