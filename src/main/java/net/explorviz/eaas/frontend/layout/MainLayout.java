package net.explorviz.eaas.frontend.layout;

import com.vaadin.flow.component.icon.VaadinIcon;
import net.explorviz.eaas.frontend.layout.component.NavigationTab;
import net.explorviz.eaas.frontend.view.ExploreView;
import net.explorviz.eaas.frontend.view.MainView;
import net.explorviz.eaas.frontend.view.NewProjectView;
import net.explorviz.eaas.frontend.view.admin.InstancesView;
import net.explorviz.eaas.frontend.view.admin.UsersView;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.User;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.security.SecurityUtils;

import java.util.Collection;
import java.util.Optional;

/**
 * Layout used for all non-project specific views. Links to the home page, lists all projects owned by the principal of
 * the current security context, and adds administration entries if permitted.
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
        addNavigationTab(NavigationTab.create("Explore", VaadinIcon.LIST_UL, ExploreView.class));
        addNavigationTab(NavigationTab.create("New project", VaadinIcon.PLUS, NewProjectView.class));

        Optional<User> user = SecurityUtils.getCurrentUser();
        if (user.isPresent()) {
            Collection<Project> projects = projectRepo.findByOwner(user.get());

            if (!projects.isEmpty()) {
                startSection("Your projects");
                for (Project p : projects) {
                    assert p.getId() != null;

                    addNavigationTab(NavigationTab.createWithParameter(p.getName(), VaadinIcon.ARCHIVE,
                        BuildsView.class, p.getId()));
                }
            }
        }

        startSection("Administration");
        addNavigationTab(NavigationTab.create("Users", VaadinIcon.USER, UsersView.class));
        addNavigationTab(NavigationTab.create("Instances", VaadinIcon.ROTATE_RIGHT, InstancesView.class));
    }
}
