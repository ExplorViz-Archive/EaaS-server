package net.explorviz.eaas.service.docker.compose;

import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.service.docker.AdapterException;
import net.explorviz.eaas.service.process.BackgroundProcess;
import org.apache.commons.lang.Validate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
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
public class DockerComposeToolImplementation implements DockerComposeAdapter {
    private static final String DOCKER_COMPOSE_COMMAND = "docker-compose";
    private static final int NORMAL_EXIT_CODE = 0;
    private static final int INITIAL_BUFFER_SIZE = 4096;

    private final long operationTimeout;

    /**
     * @param operationTimeout Timeout in milliseconds for docker-compose calls.
     */
    public DockerComposeToolImplementation(long operationTimeout) throws AdapterException {
        this.operationTimeout = operationTimeout;

        log.info(DOCKER_COMPOSE_COMMAND + " version:\n{}", runCommand(null, List.of("version")));
    }

    /**
     * Starts a docker-compose command and spawns a background task to keep reading its standard output. Standard error
     * is piped into our output.
     *
     * @param service If not {@code null}, {@link DockerComposeDefinition#getName()} will be passed to docker-compose
     *                via {@code -p name} and the {@link DockerComposeDefinition#getComposeDefinition()} will be piped
     *                into the docker-compose command and {@code -f /dev/stdin}  added to the arguments
     * @param args    The arguments to pass to docker-compose
     * @throws AdapterException If the command could not be started
     */
    protected static BackgroundProcess startCommand(@Nullable DockerComposeDefinition service, Collection<String> args)
        throws AdapterException {
        Validate.noNullElements(args, "argument may not be null");

        List<String> command = new ArrayList<>(args.size() + 6);
        command.add(DOCKER_COMPOSE_COMMAND);
        command.add("--no-ansi"); // We're not handling color codes so remove them from the output

        if (service != null) {
            command.add("-p");
            command.add(service.getName());
            command.add("-f");
            command.add("/dev/stdin");
        }
        command.addAll(args);

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);

        log.info("Running command: {}", String.join(" ", command));
        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
            log.error("Starting docker-compose process failed", e);
            throw new AdapterException("Operation failed", e);
        }

        if (service != null) {
            try (OutputStreamWriter stdin = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
                stdin.write(service.getComposeDefinition());
            } catch (IOException e) {
                log.error("Piping compose definition into docker-compose failed", e);
                throw new AdapterException("Operation failed", e);
            }
        }

        return new BackgroundProcess(process);
    }

    /**
     * Execute a docker-compose command blockingly and wait for it to finish. If the operation times out, the process
     * will be forcibly killed and an {@link AdapterException} is thrown. If the process finishes with a non-zero exit
     * code, an {@link AdapterException} is thrown
     *
     * @return Standard output of the command
     * @throws AdapterException If the command did not execute successfully
     * @see #startCommand(DockerComposeDefinition, Collection)
     */
    protected String runCommand(@Nullable DockerComposeDefinition service, Collection<String> args)
        throws AdapterException {
        StringBuilder standardOutput = new StringBuilder(INITIAL_BUFFER_SIZE);

        BackgroundProcess process = startCommand(service, args);
        process.startListening(line -> {
            standardOutput.append(line);
            standardOutput.append("\n");
        });

        try {
            if (!process.getProcess().waitFor(operationTimeout, TimeUnit.MILLISECONDS)) {
                log.error("docker-compose operation timed out, forcibly terminating process...");
                process.close();
                throw new AdapterException("Operation timed out");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int exitCode = process.getProcess().exitValue();
        if (exitCode != NORMAL_EXIT_CODE) {
            log.error("docker-compose exited with error code {}", exitCode);
            throw new AdapterException("Operation failed (error " + exitCode + "). See log for more information");
        }

        return standardOutput.toString();
    }

    @Override
    public void up(@NonNull DockerComposeDefinition service) throws AdapterException {
        runCommand(service, List.of("up", "-d"));
        service.setRunning(true);
        service.setStartedTime(ZonedDateTime.now());
    }

    @Override
    public void down(@NonNull DockerComposeDefinition service) throws AdapterException {
        runCommand(service, List.of("down", "-v"));
        service.setRunning(false);
    }

    @Override
    public String logs(@NonNull DockerComposeDefinition service, @NonNull String... serviceNames)
        throws AdapterException {
        Collection<String> args = new ArrayList<>(serviceNames.length + 1);
        args.add("logs");
        Collections.addAll(args, serviceNames);
        return runCommand(service, args);
    }

    @Override
    public BackgroundProcess logsFollow(@NonNull DockerComposeDefinition service, @NonNull String... serviceNames)
        throws AdapterException {
        Collection<String> args = new ArrayList<>(serviceNames.length + 2);
        args.add("logs");
        args.add("-f");
        Collections.addAll(args, serviceNames);
        return startCommand(service, args);
    }
}
