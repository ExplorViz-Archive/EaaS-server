package net.explorviz.eaas.service.docker;

import org.springframework.lang.NonNull;

/**
 * Provides programmatic access to docker-compose, abstracting away direct process interaction with docker-compose.
 * <p>
 * Does not keep any state about the instances being run. It is recommended to use the restart-policy "no" (which is
 * the default) in your service definitions, so all containers can easily be removed by restarting docker in case we
 * crash.
 * <p>
 * The standard implementation for this is {@link DockerComposeToolImplementation} which uses the
 * <b>docker-compose</b> command line utility.
 */
public interface DockerComposeAdapter {
    /**
     * Run a compose stack instance from the given compose file definition and give it a unique name.
     * <p>
     * This is equivalent to running <pre>docker-compose -p name -f composeFile.yml up -d</pre>
     *
     * @param name              A unique project name for this compose stack instance. May not be empty.
     * @param composeDefinition File contents of the docker-compose.yml file to run from
     */
    void up(@NonNull String name, @NonNull String composeDefinition) throws AdapterException;

    /**
     * Stops a previously started compose stack instance. Make sure to pass exactly the same parameters as when
     * starting.
     * <p>
     * This is equivalent to running <pre>docker-compose -p name -f composeFile.yml down</pre>
     *
     * @param name              A unique project name for this compose stack instance. May not be empty.
     * @param composeDefinition File contents of the docker-compose.yml file to run from
     */
    void down(@NonNull String name, @NonNull String composeDefinition) throws AdapterException;
}
