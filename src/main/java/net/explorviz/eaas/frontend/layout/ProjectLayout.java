package net.explorviz.eaas.frontend.layout;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.NotFoundException;
import net.explorviz.eaas.frontend.layout.component.NavigationTab;
import net.explorviz.eaas.frontend.view.MainView;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.frontend.view.project.SecretsView;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.repository.ProjectRepository;

public class ProjectLayout extends BaseLayout {
    private static final long serialVersionUID = 8689866379276497334L;

    private final ProjectRepository projectRepository;

    private Project project;

    public ProjectLayout(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Location param = event.getLocation().getSubLocation().orElseThrow(NotFoundException::new);
        long projectId;
        try {
            projectId = Long.parseLong(param.getFirstSegment());
        } catch (NumberFormatException e) {
            throw new NotFoundException();
        }

        project = projectRepository.findById(projectId).orElseThrow(NotFoundException::new);

        super.beforeEnter(event);
    }

    @Override
    protected void build() {
        addNavigationTab(NavigationTab.create("Back", VaadinIcon.BACKSPACE_A, MainView.class));

        startSection(project.getName());
        addNavigationTab(NavigationTab.createWithParameter("Builds", VaadinIcon.LIST, BuildsView.class,
            project.getId()));
        addNavigationTab(NavigationTab.createWithParameter("Secrets", VaadinIcon.KEY, SecretsView.class,
            project.getId()));
    }
}

