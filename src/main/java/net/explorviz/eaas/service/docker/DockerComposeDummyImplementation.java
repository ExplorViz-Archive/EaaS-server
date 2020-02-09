package net.explorviz.eaas.service.docker;

import org.springframework.lang.NonNull;

/**
 * Implements {@link DockerComposeAdapter} with all no-op methods. Can be used for testing purposes or frontend
 * development without a docker daemon present.
 */
class DockerComposeDummyImplementation implements DockerComposeAdapter {
    @Override
    public void up(@NonNull String name, @NonNull String composeDefinition) {
    }

    @Override
    public void down(@NonNull String name, @NonNull String composeDefinition) {
    }
}
