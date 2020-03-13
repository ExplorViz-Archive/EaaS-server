package net.explorviz.eaas.frontend.view.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.component.BuildControls;
import net.explorviz.eaas.frontend.component.list.ExplorVizListEntry;
import net.explorviz.eaas.frontend.component.list.RichList;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.frontend.view.DynamicView;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.service.docker.compose.DockerComposeAdapter;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;
import org.springframework.security.access.annotation.Secured;

import javax.persistence.PrePersist;
import java.util.Collection;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;
import static com.vaadin.flow.dom.ElementFactory.createParagraph;

@PageTitle("Manage Instances - " + Application.PAGE_TITLE)
@Route(value = "manage/instances", layout = MainLayout.class)
@Secured("MANAGE_INSTANCES")
public class GlobalInstancesView extends DynamicView {
    private static final long serialVersionUID = 2570920838715737622L;

    private final ExplorVizManager manager;

    private final BuildControls controls;
    private final DockerComposeAdapter dockerCompose;
    private final Button stopAllButton;

    private RichList<ExplorVizInstance> instanceList;

    public GlobalInstancesView(ExplorVizManager manager, DockerComposeAdapter dockerCompose) {
        this.manager = manager;

        controls = new BuildControls(new DummyBuild(), manager, this::onStartInstance);
        this.dockerCompose = dockerCompose;

        stopAllButton = new Button("Stop all instances");
        stopAllButton.addClickListener(click -> this.stopAllInstances());
        stopAllButton.setIcon(VaadinIcon.CLOSE.create());
        stopAllButton.setDisableOnClick(true);
        controls.add(stopAllButton);
    }

    @Override
    protected void build() {
        getElement().appendChild(createHeading2("Global Instances"));

        getElement().appendChild(createParagraph("Here you can start an instance of ExplorViz with a dummy " +
            "application image for testing purposes."));

        add(controls);

        instanceList = new RichList<>(instance -> new ExplorVizListEntry(instance, manager, dockerCompose,
            this::onStopInstance));
        Collection<ExplorVizInstance> instances = manager.getAllInstances();
        instanceList.addEntries(instances);
        add(instanceList);

        stopAllButton.setEnabled(!instances.isEmpty());
    }

    private void stopAllInstances() {
        if (manager.stopAllInstances()) {
            Notification.show("Stopped all instances");
        } else {
            Notification.show("Some instances failed to stop in time. See log for details");
        }

        rebuild();
    }

    private void onStartInstance(ExplorVizInstance instance) {
        instanceList.addEntry(instance);
        // Enable the button because we know for sure there is at least one instance
        stopAllButton.setEnabled(true);
    }

    private void onStopInstance(ExplorVizInstance instance) {
        instanceList.removeEntry(instance);
        stopAllButton.setEnabled(!manager.getAllInstances().isEmpty());
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
            setName("Dummy");
            setDockerImage(DUMMY_DOCKER_IMAGE);
        }

        @PrePersist
        public void blockPersist() {
            throw new IllegalStateException("DummyBuild may not be persisted");
        }
    }
}
