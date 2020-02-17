package net.explorviz.eaas.service.docker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class DockerService {
    @Value("${eaas.docker.useDummyImplementation}")
    private boolean useDummyImplementation;

    @Bean
    @Lazy
    public DockerAdapter standardDockerAdapter() throws AdapterException {
        return useDummyImplementation ? new DockerDummyImplementation() : new DockerJavaImplementation();
    }

    @Bean
    @Lazy
    public DockerComposeAdapter standardDockerComposeAdapter(
        @Value("${eaas.dockerCompose.timeout}") long operationTimeout) throws AdapterException {
        return useDummyImplementation ?
            new DockerComposeDummyImplementation() : new DockerComposeToolImplementation(operationTimeout);
    }
}
