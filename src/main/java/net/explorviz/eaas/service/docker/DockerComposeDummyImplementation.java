package net.explorviz.eaas.service.docker;

/**
 * Implements {@link DockerComposeAdapter} with all no-op methods. Can be used for testing purposes or frontend
 * development without a docker daemon present.
 */
class DockerComposeDummyImplementation implements DockerComposeAdapter {
    @Override
    public void up(String name, String composeDefinition) {
    }

    @Override
    public void down(String name, String composeDefinition) {
    }
}
