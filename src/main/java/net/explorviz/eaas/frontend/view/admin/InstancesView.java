package net.explorviz.eaas.frontend.view.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.component.ExplorVizListEntry;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.frontend.view.DynamicView;
import net.explorviz.eaas.service.docker.AdapterException;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;
import net.explorviz.eaas.service.explorviz.NoMoreSlotsException;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.model.entity.Project;
import org.springframework.security.access.annotation.Secured;

import java.util.Random;

@PageTitle("Instances - " + Application.PAGE_TITLE)
@Route(value = "instances", layout = MainLayout.class)
@Secured("MANAGE_INSTANCES")
public class InstancesView extends DynamicView {
    private static final long serialVersionUID = 2570920838715737622L;

    private final ExplorVizManager explorVizManager;

    private final VerticalLayout instanceList = new VerticalLayout();

    public InstancesView(ExplorVizManager explorVizManager) {
        this.explorVizManager = explorVizManager;
    }

    @Override
    protected void build() {
        add(new H2("Running Instances"));

        Button newInstanceButton = new Button("Start new instance");
        newInstanceButton.addClickListener(click -> this.doNewInstance());
        add(newInstanceButton);

        explorVizManager.getAllInstances().forEach(project -> instanceList.add(new ExplorVizListEntry(project,
            this::doStopInstance)));

        add(instanceList);
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
            instanceList.add(new ExplorVizListEntry(instance, this::doStopInstance));
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
            instanceList.remove(explorVizListEntry);
            Notification.show("Stopped instance " + instance.getName());
        } catch (AdapterException e) {
            Notification.show("Error stopping instance: " + e.getMessage());
        }
    }
}
