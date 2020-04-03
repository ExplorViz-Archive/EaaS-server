package net.explorviz.eaas.frontend.layout;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import net.explorviz.eaas.frontend.component.NavigationTab;
import net.explorviz.eaas.frontend.view.ExploreView;
import net.explorviz.eaas.frontend.view.MainView;
import net.explorviz.eaas.frontend.view.NewProjectView;
import net.explorviz.eaas.frontend.view.admin.GlobalInstancesView;
import net.explorviz.eaas.frontend.view.admin.UsersView;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.User;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.security.SecurityUtils;

import java.util.Optional;

/**
 * Layout used for all non-project specific views. Links to the home page, lists all projects owned by the principal of
 * the current security context, and adds administration entries if permitted.
 */
@Push(PushMode.MANUAL)
public class MainLayout extends NavigationLayout {
    private static final long serialVersionUID = 8689866379276497334L;

    private final ProjectRepository projectRepo;

    public MainLayout(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
    }

    @Override
    protected void buildNavigation() {
        addTab(NavigationTab.create("Home", VaadinIcon.HOME.create(), MainView.class));
        addTab(NavigationTab.create("Explore", VaadinIcon.LIST_UL.create(), ExploreView.class));
        addTab(NavigationTab.create("New project", VaadinIcon.PLUS.create(), NewProjectView.class));

        Optional<User> user = SecurityUtils.getCurrentUser();
        if (user.isPresent()) {
            startSection("Your projects");
            for (Project p : projectRepo.findByOwner(user.get())) {
                assert p.getId() != null : "Project fetched from database has no ID";

                addTab(NavigationTab.createWithParameter(p.getName(), VaadinIcon.ARCHIVE.create(), BuildsView.class,
                    p.getId()));
            }
        }

        startSection("Administration");
        addTab(NavigationTab.create("Instances", VaadinIcon.ROTATE_RIGHT.create(), GlobalInstancesView.class));
        addTab(NavigationTab.create("Users", VaadinIcon.USERS.create(), UsersView.class));
    }
}
