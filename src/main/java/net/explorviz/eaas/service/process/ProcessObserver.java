package net.explorviz.eaas.service.process;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
class ProcessObserver implements Runnable {
    private static final int INITIAL_BUFFER_SIZE = 512;
    private static final int MAX_LINES = 256;
    private static final int MAX_BYTES = 64 * 1024;

    private final Process process;
    private final ProcessListener listener;

    ProcessObserver(Process process, ProcessListener listener) {
        this.process = process;
        this.listener = listener;
    }

    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),
            StandardCharsets.UTF_8), MAX_BYTES)) {
            StringBuilder text = new StringBuilder(INITIAL_BUFFER_SIZE);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);

                for (int count = 1; bufferedReader.ready() && count < MAX_LINES && text.length() < MAX_BYTES &&
                    (line = bufferedReader.readLine()) != null; count++) {
                    text.append("\n");
                    text.append(line);
                }

                listener.onStandardOutput(text.toString());
                text.setLength(0);
            }

            listener.onDied(process.waitFor());
        } catch (IOException e) {
            log.warn("Error reading background process output", e);
        } catch (InterruptedException ignored) {
        }
    }
}
