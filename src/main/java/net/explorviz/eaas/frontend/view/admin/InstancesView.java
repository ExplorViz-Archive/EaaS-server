package net.explorviz.eaas.frontend.view.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.component.list.ExplorVizListEntry;
import net.explorviz.eaas.frontend.component.list.SimpleList;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.frontend.view.DynamicView;
import net.explorviz.eaas.service.docker.AdapterException;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;
import net.explorviz.eaas.service.explorviz.NoMoreSlotsException;
import net.explorviz.eaas.model.entity.Build;
import org.springframework.security.access.annotation.Secured;

import javax.persistence.PrePersist;
import java.util.Collection;

@PageTitle("Instances - " + Application.PAGE_TITLE)
@Route(value = "instances", layout = MainLayout.class)
@Secured("MANAGE_INSTANCES")
public class InstancesView extends DynamicView {
    private static final long serialVersionUID = 2570920838715737622L;

    private final ExplorVizManager explorVizManager;

    private final Button testInstanceButton;
    private final Button stopAllButton;

    private SimpleList<ExplorVizInstance> instanceList;

    public InstancesView(ExplorVizManager explorVizManager) {
        this.explorVizManager = explorVizManager;

        testInstanceButton = new Button("Start test instance");
        testInstanceButton.addClickListener(click -> this.startTestInstance());
        testInstanceButton.setDisableOnClick(true);

        stopAllButton = new Button("Stop all instances");
        stopAllButton.addClickListener(click -> this.stopAllInstances());
        stopAllButton.setIcon(VaadinIcon.CLOSE.create());
        stopAllButton.setDisableOnClick(true);
    }

    @Override
    protected void build() {
        add(new H2("Running Instances"));

        HorizontalLayout controls = new HorizontalLayout();
        controls.add(testInstanceButton);
        controls.add(stopAllButton);
        add(controls);

        instanceList = new SimpleList<>(instance -> new ExplorVizListEntry(instance, this::doStopInstance));
        Collection<ExplorVizInstance> instances = explorVizManager.getAllInstances();
        instanceList.addEntries(instances);
        add(instanceList);

        stopAllButton.setEnabled(!instances.isEmpty());
    }

    private void stopAllInstances() {
        if (explorVizManager.stopAllInstances()) {
            Notification.show("Stopped all instances");
        } else {
            Notification.show("Some instances failed to stop in time. See log for details");
        }

        rebuild();
    }

    private void startTestInstance() {
        try {
            ExplorVizInstance instance = explorVizManager.startInstance(new DummyBuild(), "1.5.0");
            instanceList.addEntry(instance);
            // Enable the button because we know for sure there is at least one instance
            stopAllButton.setEnabled(true);
            Notification.show("Started new instance " + instance.getName());
        } catch (AdapterException e) {
            Notification.show("Error starting instance: " + e.getMessage());
        } catch (NoMoreSlotsException e) {
            Notification.show(e.getMessage());
        }

        testInstanceButton.setEnabled(true);
    }

    private void doStopInstance(ExplorVizInstance instance) {
        try {
            explorVizManager.stopInstance(instance);
            instanceList.removeEntry(instance);
            stopAllButton.setEnabled(!explorVizManager.getAllInstances().isEmpty());
            Notification.show("Stopped instance " + instance.getName());
        } catch (AdapterException e) {
            Notification.show("Error stopping instance: " + e.getMessage());
        }
    }

    private static final class DummyBuild extends Build {
        private static final long serialVersionUID = 6375871599507575250L;

        /**
         * This is a minimal container image immediately exiting with success. See
         * <a href="https://hub.docker.com/r/tianon/true/">tianon/true on Docker Hub</a>.
         */
        public static final String DUMMY_DOCKER_IMAGE = "tianon/true:latest";

        private DummyBuild() {
            setId(0L);
            setNew(false);
            setName("Dummy");
            setDockerImage(DUMMY_DOCKER_IMAGE);
        }

        @PrePersist
        public void blockPersist() {
            throw new IllegalStateException("DummyBuild may not be persisted");
        }
    }
}
