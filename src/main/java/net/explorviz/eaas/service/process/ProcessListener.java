package net.explorviz.eaas.service.process;

/**
 * Used to provide updates about a {@link BackgroundProcess}.
 */
@FunctionalInterface
public interface ProcessListener {
    /**
     * Called with the exit code of the process when it dies and is not killed through {@link BackgroundProcess#kill()}}
     * or any other means.
     */
    default void onDied(int exitCode) {
    }

    /**
     * Called for each line the process writes to its standard output.
     */
    void onStandardOutput(String line);
}
