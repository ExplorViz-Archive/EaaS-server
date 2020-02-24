package net.explorviz.eaas.service.process;

import lombok.Getter;
import org.springframework.lang.NonNull;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents a currently running process, observed from a background thread.
 * <p>
 * Continuously reads from the standard output of the given {@link Process} and passes each line to a {@link
 * ProcessListener} in a background thread.
 */
public class BackgroundProcess implements Closeable {
    @Getter
    private Process process;

    private ExecutorService executor;

    protected BackgroundProcess() {
    }

    public BackgroundProcess(@NonNull Process process) {
        this.process = process;
    }

    /**
     * Set the listener to pass standard output to.
     */
    public void startListening(@NonNull ProcessListener listener) {
        if (executor != null) {
            throw new IllegalStateException("Already started listening to this process");
        }

        executor = Executors.newSingleThreadExecutor();
        executor.submit(new ProcessObserver(process, listener));
    }

    /**
     * Kill the process if it is still running and stop the background thread reading from the standard output. {@link
     * ProcessListener#onDied(int)} might not be called.
     */
    @Override
    public void close() {
        if (executor != null) {
            executor.shutdownNow();
        }

        process.destroyForcibly();
    }
}
