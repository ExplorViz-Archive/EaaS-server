package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import net.explorviz.eaas.service.docker.AdapterException;
import net.explorviz.eaas.service.docker.compose.DockerComposeAdapter;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;
import net.explorviz.eaas.service.process.BackgroundProcess;

import java.util.function.Consumer;

/**
 * A horizontal bar with controls for a running ExplorVizInstance.
 * <p>
 * If there is no instance for a build yet use {@link BuildControls} instead.
 */
public class InstanceControls extends HorizontalLayout implements BeforeLeaveObserver {
    private static final long serialVersionUID = 7716806751192597483L;

    private final ExplorVizInstance instance;
    private final ExplorVizManager manager;
    private final DockerComposeAdapter dockerCompose;
    private final Consumer<? super ExplorVizInstance> stopCallback;
    private final Button logButton;
    private final Button fullLogButton;

    private BackgroundProcess logProcess;

    /**
     * @param stopCallback Will be called when the user clicks on the {@code Stop} button, after the instance has been
     *                     stopped succesfully through the given {@link ExplorVizManager}
     */
    public InstanceControls(ExplorVizInstance instance, ExplorVizManager manager, DockerComposeAdapter dockerCompose,
                            Consumer<? super ExplorVizInstance> stopCallback) {
        this.instance = instance;
        this.manager = manager;
        this.dockerCompose = dockerCompose;
        this.stopCallback = stopCallback;

        addClassName("controls-bar");

        Anchor link = IconAnchor.createFromImage(instance.getAccessURL(), "icons/icon-48x48.png", "Open");
        link.setTarget("_blank");
        add(link);

        Button stopButton = new Button("Stop");
        stopButton.addClickListener(click -> this.doStopInstance());
        stopButton.setIcon(VaadinIcon.CLOSE_SMALL.create());
        stopButton.setDisableOnClick(true);
        add(stopButton);

        logButton = new Button("Application Logs");
        logButton.addClickListener(click -> this.openLogs(click.getSource().getUI().orElseThrow(
            () -> new IllegalStateException("Button was clicked by a ghost")),
            ExplorVizInstance.APPLICATION_SERVICE_NAME));
        logButton.setIcon(VaadinIcon.CLIPBOARD_TEXT.create());
        logButton.setDisableOnClick(true);
        add(logButton);

        fullLogButton = new Button("ExplorViz Logs");
        fullLogButton.addClickListener(click -> this.openLogs(click.getSource().getUI().orElseThrow(
            () -> new IllegalStateException("Button was clicked by a ghost"))));
        fullLogButton.setIcon(VaadinIcon.CLIPBOARD_TEXT.create());
        fullLogButton.setDisableOnClick(true);
        add(fullLogButton);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        stopLogs();
        super.onDetach(detachEvent);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        stopLogs();
    }

    private void openLogs(UI ui, String... serviceNames) {
        logButton.setEnabled(true);
        fullLogButton.setEnabled(true);

        try {
            logProcess = dockerCompose.logsFollow(instance, serviceNames);
            LogDialog dialog = new LogDialog(instance.getBuild().getName(), ui, ignored -> stopLogs());
            logProcess.startListening(dialog);
            dialog.open();
        } catch (AdapterException e) {
            Notification.show("Failed to read logs: " + e.getMessage());
        }
    }

    private void stopLogs() {
        if (logProcess != null) {
            logProcess.close();
            logProcess = null;
        }

        logButton.setEnabled(true);
        fullLogButton.setEnabled(true);
    }

    private void doStopInstance() {
        try {
            manager.stopInstance(instance);
            Notification.show("Stopped instance " + instance.getName());
        } catch (AdapterException e) {
            Notification.show("Error stopping instance: " + e.getMessage());
            return;
        }

        stopCallback.accept(instance);
    }
}
