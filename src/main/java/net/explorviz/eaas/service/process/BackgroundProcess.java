package net.explorviz.eaas.service.process;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents a currently running process, observed from a background thread.
 * <p>
 * Continuously reads from the standard output of the given {@link Process} and passes each line to a {@link
 * ProcessListener} in a background thread.
 */
@Slf4j
public class BackgroundProcess {
    @Getter
    private Process process;

    private ExecutorService executor;

    protected BackgroundProcess() {
    }

    public BackgroundProcess(@NonNull Process process) {
        this.process = process;
    }

    /**
     * Kill the process if it is still running and stop the background thread reading from the standard output. {@link
     * ProcessListener#onDied(int)} might not be called.
     */
    public void kill() {
        if (executor != null) {
            executor.shutdownNow();
        }

        process.destroyForcibly();
    }

    /**
     * Set the listener to pass standard output to.
     */
    public void startListening(@NonNull ProcessListener listener) {
        if (executor != null) {
            throw new IllegalStateException("Already started listening to this process");
        }

        executor = Executors.newSingleThreadExecutor();
        executor.submit(new ProcessObserver(listener));
    }

    private final class ProcessObserver implements Runnable {
        private final ProcessListener listener;

        private ProcessObserver(ProcessListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),
                StandardCharsets.UTF_8))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    listener.onStandardOutput(line);
                }

                listener.onDied(process.waitFor());
            } catch (IOException e) {
                log.warn("Error reading process output", e);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
