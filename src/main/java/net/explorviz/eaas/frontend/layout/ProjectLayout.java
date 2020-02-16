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

/**
 * Layout used for all project-specific views, with sidebar menu entries for that project.
 */
public class ProjectLayout extends NavigationLayout {
    private static final long serialVersionUID = 8689866379276497334L;

    private final ProjectRepository projectRepository;

    private Project project;

    public ProjectLayout(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Project> optionalProject = parseProjectParameter(event.getLocation());

        if (optionalProject.isEmpty()) {
            event.rerouteToError(NotFoundException.class, "Project not found");
            return;
        }

        project = optionalProject.get();

        super.beforeEnter(event);
    }

    @Override
    protected void buildNavigation() {
        addNavigationTab(NavigationTab.create("Back", VaadinIcon.BACKSPACE_A.create(), MainView.class));

        assert project.getId() != null : "Project fetched from database has no ID";

        startSection(project.getName());
        addNavigationTab(
            NavigationTab.createWithParameter("Builds", VaadinIcon.LIST.create(), BuildsView.class, project.getId()));
        addNavigationTab(
            NavigationTab.createWithParameter("Secrets", VaadinIcon.KEY.create(), SecretsView.class, project.getId()));
        addNavigationTab(
            NavigationTab.createWithParameter("Settings", VaadinIcon.COG.create(), SettingsView.class,
                project.getId()));
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

