package net.explorviz.eaas.service.docker;

import net.explorviz.eaas.service.process.BackgroundProcess;
import net.explorviz.eaas.service.process.ProcessListener;
import org.springframework.lang.NonNull;

/**
 * Provides programmatic access to docker-compose, abstracting away direct process interaction with docker-compose.
 * <p>
 * Does not keep any state about the instances being run. It is recommended to use the restart-policy "no" (which is the
 * default) in your service definitions, so all containers can easily be removed by restarting docker in case we crash.
 * <p>
 * The standard implementation for this is {@link DockerComposeToolImplementation} which uses the
 * <b>docker-compose</b> command line utility.
 */
public interface DockerComposeAdapter {
    /**
     * Run a compose service instance from the given compose service definition.
     * <p>
     * This is equivalent to running <pre>docker-compose -p name -f composeFile.yml up -d</pre>
     *
     * @param service The docker-compose definition to start
     */
    void up(@NonNull DockerComposeDefinition service) throws AdapterException;

    /**
     * Stops a previously started compose service instance.
     * <p>
     * This is equivalent to running <pre>docker-compose -p name -f composeFile.yml down -v</pre>
     *
     * @param service The docker-compose definition to stop
     */
    void down(@NonNull DockerComposeDefinition service) throws AdapterException;

    /**
     * Fetch container output.
     * <p>
     * This is equivalent to running <pre>docker-compose -p name -f composeFile.yml logs serviceNames...</pre>
     *
     * @param service      The docker-compose definition  to obtain logs for
     * @param serviceNames Names of the services specified in the {@link DockerComposeDefinition#getComposeDefinition()}
     *                     to obtain logs from
     * @return The raw log text, seperated by newlines
     */
    String logs(@NonNull DockerComposeDefinition service, @NonNull String... serviceNames) throws AdapterException;

    /**
     * Continuously read container output. Call {@link BackgroundProcess#startListening(ProcessListener)} on the
     * returned object to start obtaining logs and be notified when the service stops.
     * <p>
     * This is equivalent to running
     * <pre>docker-compose -p name -f composeFile.yml logs -f serviceNames...</pre>
     *
     * @return A BackgroundProcess to observe the {@code docker-compose} command that is reading the logs
     * @see #logs(DockerComposeDefinition, String...)
     */
    BackgroundProcess logsFollow(@NonNull DockerComposeDefinition service, @NonNull String... serviceNames)
        throws AdapterException;
}
