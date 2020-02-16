package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import net.explorviz.eaas.service.docker.AdapterException;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;

import java.util.function.Consumer;

/**
 * A horizontal bar with controls for a running ExplorVizInstance.
 * <p>
 * If there is no instance for a build yet use {@link BuildControls} instead.
 */
public class InstanceControls extends HorizontalLayout {
    private static final long serialVersionUID = 7716806751192597483L;

    private final ExplorVizInstance instance;
    private final ExplorVizManager manager;
    private final Consumer<? super ExplorVizInstance> stopCallback;

    /**
     * @param stopCallback Will be called when the user clicks on the {@code Stop} button, after the instance has been
     *                       stopped succesfully through the given {@link ExplorVizManager}
     */
    public InstanceControls(ExplorVizInstance instance, ExplorVizManager manager,
                            Consumer<? super ExplorVizInstance> stopCallback) {
        this.instance = instance;
        this.manager = manager;
        this.stopCallback = stopCallback;

        addClassName("controls-bar");

        Anchor link = IconAnchor.createFromImage(instance.getAccessURL(), "icons/icon-48x48.png", "Open");
        link.setTarget("_blank");
        add(link);

        Button stopButton = new Button("Stop");
        stopButton.addClickListener(click -> this.doStop());
        stopButton.setIcon(VaadinIcon.CLOSE_SMALL.create());
        stopButton.setDisableOnClick(true);
        add(stopButton);
    }

    private void doStop() {
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
