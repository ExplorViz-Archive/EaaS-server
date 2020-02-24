package net.explorviz.eaas.service.process;

/**
 * Used to provide updates about a {@link BackgroundProcess}.
 */
@FunctionalInterface
public interface ProcessListener {
    /**
     * Called with the exit code of the process when it dies and is not killed through {@link BackgroundProcess#close()}
     * or any other means.
     */
    default void onDied(int exitCode) {
    }

    /**
     * Called when standard output happens. May contain multiple lines separated with a single newline character. No
     * trailing newline.
     */
    void onStandardOutput(String text);
}
