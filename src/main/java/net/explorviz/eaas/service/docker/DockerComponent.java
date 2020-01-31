package net.explorviz.eaas.service.docker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class DockerComponent {
    @Bean
    @Lazy
    public DockerAdapter standardDockerAdapter() throws AdapterException {
        return new DockerJavaImplementation();
    }

    @Bean
    @Lazy
    public DockerComposeAdapter standardDockerComposeAdapter(
        @Value("${eaas.docker-compose.timeout:300000}") long operationTimeout) throws AdapterException {
        return new DockerComposeToolImplementation(operationTimeout);
    }
}
