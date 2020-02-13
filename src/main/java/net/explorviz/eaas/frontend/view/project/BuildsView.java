package net.explorviz.eaas.frontend.view.project;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.frontend.layout.ProjectLayout;
import net.explorviz.eaas.model.repository.BuildRepository;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;
import org.springframework.security.access.annotation.Secured;

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
        add(new H2("Builds"));

        // TODO: Builds view
    }
}
