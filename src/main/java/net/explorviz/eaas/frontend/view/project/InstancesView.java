package net.explorviz.eaas.frontend.view.project;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.frontend.component.list.BuildListEntry;
import net.explorviz.eaas.frontend.component.list.RichList;
import net.explorviz.eaas.frontend.layout.ProjectLayout;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.security.SecurityUtils;
import net.explorviz.eaas.service.docker.compose.DockerComposeAdapter;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;
import static com.vaadin.flow.dom.ElementFactory.createParagraph;

@Route(value = "instances", layout = ProjectLayout.class)
@Secured("RUN_BUILD")
public class InstancesView extends AbstractProjectView {
    private static final long serialVersionUID = -2777916910494366724L;

    private final ExplorVizManager explorVizManager;
    private final DockerComposeAdapter dockerCompose;

    public InstancesView(ProjectRepository projectRepo, ExplorVizManager explorVizManager,
                         DockerComposeAdapter dockerCompose) {
        super(projectRepo, "Instances");
        this.explorVizManager = explorVizManager;
        this.dockerCompose = dockerCompose;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        assert getProject() != null;

        // Instances view only requires RUN_BUILD authority unless project is configured as hidden
        if (getProject().isHidden() && !SecurityUtils.hasReadAccess(getProject())) {
            throw new AccessDeniedException("You do not have permission to access this page.");
        }

        super.beforeEnter(event);
    }

    @Override
    public void build() {
        getElement().appendChild(createHeading2("Running Instances"));

        Collection<Build> runningBuilds =
            explorVizManager.getAllInstances().stream()
                .map(ExplorVizInstance::getBuild)
                .filter(build -> build.getProject() != null && getProject().getId().equals(build.getProject().getId()))
                .collect(Collectors.toList());

        if (runningBuilds.isEmpty()) {
            getElement().appendChild(
                createParagraph("No instances are running right now."),
                createParagraph("Go to the Builds page to start an instance for a build.")
            );
        } else {
            /*
             * Note we deliberately do not remove builds from the list when the user stops them, so the user doesn't
             * have to find the build on the Builds view again if they want to restart it. Stopped builds are removed
             * when the user leaves and (re)enters the view.
             */
            RichList<Build> buildList = new RichList<>(build -> new BuildListEntry(build, explorVizManager,
                dockerCompose));
            buildList.addEntries(runningBuilds);
            add(buildList);
        }
    }
}
