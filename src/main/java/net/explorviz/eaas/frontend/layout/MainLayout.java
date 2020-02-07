package net.explorviz.eaas.frontend.layout;

import com.vaadin.flow.component.icon.VaadinIcon;
import net.explorviz.eaas.frontend.layout.component.NavigationTab;
import net.explorviz.eaas.frontend.view.MainView;
import net.explorviz.eaas.frontend.view.admin.InstancesView;
import net.explorviz.eaas.frontend.view.admin.UsersView;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.repository.ProjectRepository;

import java.util.Collection;

/**
 * Layout used for all non-project specific views. Adds administration entries over the {@link BaseLayout} if the
 * current security context has the necessary authority to enter them.
 */
public class MainLayout extends BaseLayout {
    private static final long serialVersionUID = 8689866379276497334L;

    private final ProjectRepository projectRepo;

    public MainLayout(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
    }

    @Override
    protected void build() {
        addNavigationTab(NavigationTab.create("Home", VaadinIcon.HOME, MainView.class));

        // TODO: Also list owned projects
        Collection<Project> projects = projectRepo.findByHidden(false);
        if (!projects.isEmpty()) {
            startSection("Projects");
            for (Project p : projects) {
                addNavigationTab(NavigationTab.createWithParameter(p.getName(), VaadinIcon.ARCHIVE, BuildsView.class,
                    p.getId()));
            }
        }

        startSection("Administration");
        addNavigationTab(NavigationTab.create("Users", VaadinIcon.USER, UsersView.class));
        addNavigationTab(NavigationTab.create("Instances", VaadinIcon.ROTATE_RIGHT, InstancesView.class));
    }
}
