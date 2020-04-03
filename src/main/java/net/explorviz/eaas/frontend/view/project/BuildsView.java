package net.explorviz.eaas.frontend.view.project;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.frontend.component.list.BuildListEntry;
import net.explorviz.eaas.frontend.component.list.RichList;
import net.explorviz.eaas.frontend.layout.ProjectLayout;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.model.repository.BuildRepository;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.security.SecurityUtils;
import net.explorviz.eaas.service.docker.compose.DockerComposeAdapter;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;
import static com.vaadin.flow.dom.ElementFactory.createParagraph;

@Route(value = "builds", layout = ProjectLayout.class)
public class BuildsView extends AbstractProjectView {
    private static final long serialVersionUID = -2777916910494366724L;

    private final BuildRepository buildRepo;
    private final ExplorVizManager explorVizManager;
    private final DockerComposeAdapter dockerCompose;

    public BuildsView(ProjectRepository projectRepo, BuildRepository buildRepo, ExplorVizManager explorVizManager,
                      DockerComposeAdapter dockerCompose) {
        super(projectRepo, "Builds");
        this.buildRepo = buildRepo;
        this.explorVizManager = explorVizManager;
        this.dockerCompose = dockerCompose;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        assert getProject() != null;

        // Builds view is public even without any authorizations unless project is configured as hidden
        if (getProject().isHidden() && !SecurityUtils.hasReadAccess(getProject())) {
            throw new AccessDeniedException("You do not have permission to access this page.");
        }

        super.beforeEnter(event);
    }

    @Override
    public void build() {
        getElement().appendChild(createHeading2("Builds"));

        // TODO: Paging
        Page<Build> builds = buildRepo.findByProjectOrderByCreatedDateDesc(getProject(), Pageable.unpaged());

        if (builds.isEmpty()) {
            getElement().appendChild(createParagraph("No builds have been added yet."));

            if (SecurityUtils.hasManageAccess(getProject())) {
                getElement().appendChild(
                    createParagraph("Go to the Secrets page and create a secret to start adding builds."));
            }
        } else {
            RichList<Build> buildList = new RichList<>(build -> new BuildListEntry(build, explorVizManager,
                dockerCompose));
            buildList.addEntries(builds);
            add(buildList);
        }
    }
}
