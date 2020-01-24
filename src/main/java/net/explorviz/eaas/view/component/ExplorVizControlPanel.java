package net.explorviz.eaas.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import net.explorviz.eaas.docker.AdapterException;
import net.explorviz.eaas.explorviz.ExplorVizInstance;
import net.explorviz.eaas.explorviz.ExplorVizManager;
import net.explorviz.eaas.explorviz.NoMoreSlotsException;
import net.explorviz.eaas.model.Build;
import net.explorviz.eaas.model.Project;

import java.util.Random;

public class ExplorVizControlPanel extends VerticalLayout {
    private static final long serialVersionUID = 2570920838715737622L;

    private final ExplorVizManager explorVizManager;
    private final VerticalLayout explorVizInstanceList = new VerticalLayout();

    public ExplorVizControlPanel(ExplorVizManager explorVizManager) {
        this.explorVizManager = explorVizManager;

        add(new H3("ExplorViz instances"));

        Button newInstanceButton = new Button("Start new instance");
        newInstanceButton.addClickListener(click -> this.doNewInstance());
        add(newInstanceButton);

        explorVizManager.getAllInstances().forEach(project -> explorVizInstanceList.add(new ExplorVizListEntry(project,
            this::doStopInstance)));

        add(explorVizInstanceList);
    }

    private void doNewInstance() {
        // Create some dummy data
        Random random = new Random();
        Project project = new Project("Dummy project", null);
        project.setId((long) random.nextInt(100));
        Build build = new Build("Dummy build", project, "tianon/true:latest");
        build.setId((long) random.nextInt(1000));

        try {
            ExplorVizInstance instance = explorVizManager.startInstance(build, "1.5.0");
            explorVizInstanceList.add(new ExplorVizListEntry(instance, this::doStopInstance));
            Notification.show("Started new instance " + instance.getName());
        } catch (AdapterException e) {
            Notification.show("Error starting instance: " + e.getMessage());
        } catch (NoMoreSlotsException e) {
            Notification.show(e.getMessage());
        }
    }

    private void doStopInstance(ExplorVizListEntry explorVizListEntry) {
        ExplorVizInstance instance = explorVizListEntry.getInstance();
        try {
            explorVizManager.stopInstance(instance);
            explorVizInstanceList.remove(explorVizListEntry);
            Notification.show("Stopped instance " + instance.getName());
        } catch (AdapterException e) {
            Notification.show("Error stopping instance: " + e.getMessage());
        }
    }
}
