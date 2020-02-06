package net.explorviz.eaas.service.docker;

import java.io.InputStream;

/**
 * Implements {@link DockerAdapter} with all no-op methods. Can be used for testing purposes or frontend development
 * without a docker daemon present.
 */
class DockerDummyImplementation implements DockerAdapter {
    @Override
    public void loadImage(InputStream input) {
    }

    @Override
    public void deleteImage(String image) {
    }
}
