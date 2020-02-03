package net.explorviz.eaas.frontend.layout;

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
        addNavigationTab("Home", MainView.class);

        startSection("Administration");
        addNavigationTab("Users", UsersView.class);
        addNavigationTab("ExplorViz Instances", InstancesView.class);

        // TODO: Also list owned projects
        Collection<Project> projects = projectRepo.findByHidden(false);
        if (!projects.isEmpty()) {
            startSection("Projects");
            for (Project p : projects) {
                addNavigationTab(p.getName(), BuildsView.class, p.getId());
            }
        }
    }
}
