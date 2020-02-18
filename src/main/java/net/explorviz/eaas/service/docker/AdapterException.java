package net.explorviz.eaas.service.docker;

import net.explorviz.eaas.service.docker.compose.DockerComposeAdapter;

/**
 * Indicates a problem occurred in a {@link DockerAdapter} or {@link DockerComposeAdapter} when trying to perform
 * a requested operation.
 */
public class AdapterException extends Exception {
    private static final long serialVersionUID = -5312077762863844340L;

    public AdapterException(String message) {
        super(message);
    }

    public AdapterException(String message, Throwable cause) {
        super(message, cause);
    }
}
