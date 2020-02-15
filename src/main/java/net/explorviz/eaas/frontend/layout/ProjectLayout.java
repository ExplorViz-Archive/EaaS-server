package net.explorviz.eaas.frontend.layout;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.NotFoundException;
import net.explorviz.eaas.frontend.layout.component.NavigationTab;
import net.explorviz.eaas.frontend.view.MainView;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.frontend.view.project.SecretsView;
import net.explorviz.eaas.frontend.view.project.SettingsView;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.repository.ProjectRepository;

import java.util.Optional;

public class ProjectLayout extends AbstractLayout {
    private static final long serialVersionUID = 8689866379276497334L;

    private final ProjectRepository projectRepository;

    private Project project;

    public ProjectLayout(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        project = parseProjectParameter(event.getLocation())
                      .orElseThrow(() -> new NotFoundException("Project not found"));

        super.beforeEnter(event);
    }

    @Override
    protected void build() {
        addNavigationTab(NavigationTab.create("Back", VaadinIcon.BACKSPACE_A, MainView.class));

        assert project.getId() != null;

        startSection(project.getName());
        addNavigationTab(
            NavigationTab.createWithParameter("Builds", VaadinIcon.LIST, BuildsView.class, project.getId()));
        addNavigationTab(
            NavigationTab.createWithParameter("Secrets", VaadinIcon.KEY, SecretsView.class, project.getId()));
        addNavigationTab(
            NavigationTab.createWithParameter("Settings", VaadinIcon.COG, SettingsView.class, project.getId()));
    }

    private Optional<Project> parseProjectParameter(Location location) {
        try {
            return location.getSubLocation()
                       .map(Location::getFirstSegment)
                       .map(Long::parseLong)
                       .flatMap(projectRepository::findById);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}

