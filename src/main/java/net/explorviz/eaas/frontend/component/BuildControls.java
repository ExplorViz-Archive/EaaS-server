package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.service.docker.AdapterException;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;
import net.explorviz.eaas.service.explorviz.NoMoreSlotsException;

import java.util.function.Consumer;

/**
 * A horizontal bar with controls for starting a new ExplorVizInstance for a given {@link Build}.
 * <p>
 * If there already is a running instance use {@link InstanceControls} instead.
 */
public class BuildControls extends HorizontalLayout {
    private static final long serialVersionUID = 7138976677696332911L;

    private final Build build;
    private final ExplorVizManager manager;
    private final Consumer<? super ExplorVizInstance> runCallback;

    private final Select<String> versionSelection;
    private final Button runButton;

    public BuildControls(Build build, ExplorVizManager manager,
                         Consumer<? super ExplorVizInstance> runCallback) {
        this.build = build;
        this.manager = manager;
        this.runCallback = runCallback;

        addClassName("controls-bar");

        versionSelection = new Select<>();
        versionSelection.setLabel("ExplorViz version");
        versionSelection.setItems(ExplorVizManager.EXPLORVIZ_VERSIONS);
        versionSelection.setValue(manager.getDefaultVersion());
        add(versionSelection);

        runButton = new Button("Run");
        runButton.addClickListener(click -> this.runBuild());
        runButton.setIcon(VaadinIcon.CHEVRON_RIGHT_SMALL.create());
        runButton.setDisableOnClick(true);
        add(runButton);
    }

    private void runBuild() {
        try {
            ExplorVizInstance instance = manager.startInstance(build, versionSelection.getValue());
            Notification.show("Started new instance " + instance.getName());
            runCallback.accept(instance);
            runButton.setEnabled(true);
        } catch (AdapterException e) {
            Notification.show("Error starting instance: " + e.getMessage());
        } catch (NoMoreSlotsException e) {
            Notification.show(e.getMessage());
        }
    }
}
