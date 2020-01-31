package net.explorviz.eaas.service.docker;

import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides programmatic access to the Docker API.
 * <p>
 * The standard implementation for this is {@link DockerJavaImplementation} which uses the <b>docker-java</b>
 * library. This library isn't maintained very well and at some point we might want to consider switching to the
 * <b>docker-cli</b> utility instead, operating in the same way as {@link DockerComposeToolImplementation}.
 */
public interface DockerAdapter {
    /**
     * Load an image from a streamed tar archive into the docker daemon.
     *
     * @param input Stream to read the image from
     */
    void loadImage(@NonNull InputStream input) throws AdapterException, IOException;

    /**
     * Delete an image from the host system.
     *
     * @param image Image ID or tag to delete
     */
    void deleteImage(@NonNull String image) throws AdapterException;
}
