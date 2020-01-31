package net.explorviz.eaas.service.explorviz;

/**
 * Thrown by {@link ExplorVizManager} to indicate an {@link ExplorVizInstance} couldn't be started because we've hit
 * the user-specified limit of concurrently active ExplorViz instances and cannot start any more.
 */
public class NoMoreSlotsException extends Exception {
    private static final long serialVersionUID = 8138362689038905032L;

    public NoMoreSlotsException(String message) {
        super(message);
    }
}
