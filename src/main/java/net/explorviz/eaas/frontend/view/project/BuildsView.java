package net.explorviz.eaas.frontend.view.project;

import com.vaadin.flow.router.Route;
import net.explorviz.eaas.frontend.component.list.BuildListEntry;
import net.explorviz.eaas.frontend.component.list.SimpleList;
import net.explorviz.eaas.frontend.layout.ProjectLayout;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.model.repository.BuildRepository;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.security.Authorities;
import net.explorviz.eaas.security.SecurityUtils;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;
import static com.vaadin.flow.dom.ElementFactory.createParagraph;

@Route(value = "builds", layout = ProjectLayout.class)
public class BuildsView extends ProjectView {
    private static final long serialVersionUID = -2777916910494366724L;

    private final BuildRepository buildRepo;
    private final ExplorVizManager explorVizManager;

    public BuildsView(ProjectRepository projectRepo, BuildRepository buildRepo, ExplorVizManager explorVizManager) {
        super(projectRepo, "Builds");
        this.buildRepo = buildRepo;
        this.explorVizManager = explorVizManager;
    }

    @Override
    public void build() {
        getElement().appendChild(createHeading2("Builds"));

        // TODO: Display currently running builds on top or add a general project page containing live + recent builds

        // TODO: Paging
        Page<Build> builds = buildRepo.findByProjectOrderByCreatedDateDesc(getProject(), Pageable.unpaged());

        if (builds.isEmpty()) {
            getElement().appendChild(createParagraph("No builds have been added yet."));

            if (SecurityUtils.hasAuthority(Authorities.MANAGE_PROJECT_AUTHORITY)) {
                getElement().appendChild(
                    createParagraph("Go to the Secrets page and create a secret to start adding builds."));
            }
        } else {
            SimpleList<Build> buildList = new SimpleList<>(build -> new BuildListEntry(build, explorVizManager));
            buildList.addEntries(builds);
            add(buildList);
        }
    }
}
