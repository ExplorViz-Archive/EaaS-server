package net.explorviz.eaas.service.docker;

import org.springframework.lang.NonNull;

import java.io.InputStream;

/**
 * Implements {@link DockerAdapter} with all no-op methods. Can be used for testing purposes or frontend development
 * without a docker daemon present.
 */
class DockerDummyImplementation implements DockerAdapter {
    @Override
    public void loadImage(@NonNull InputStream input) {
    }

    @Override
    public void deleteImage(@NonNull String image) {
    }
}
