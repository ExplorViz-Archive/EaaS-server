package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.component.ProjectListEntry;
import net.explorviz.eaas.frontend.component.RecentlyUpdatedProjectListEntry;
import net.explorviz.eaas.frontend.component.SimpleList;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.User;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.security.SecurityUtils;

import java.util.Optional;

@PageTitle("Explore - " + Application.PAGE_TITLE)
@Route(value = "explore", layout = MainLayout.class)
public class ExploreView extends DynamicView {
    private static final long serialVersionUID = 2504755522547462480L;

    private final ProjectRepository projectRepo;

    public ExploreView(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
    }

    @Override
    protected void build() {
        add(new H2("All projects"));

        // TODO: Paging
        Optional<User> user = SecurityUtils.getCurrentUser();
        Iterable<Project> projects;
        if (user.isPresent()) {
            projects = projectRepo.findByHiddenOrOwner(false, user.get());
        } else {
            projects = projectRepo.findByHidden(false);
        }

        add(new SimpleList<>(projects, ProjectListEntry::new));
    }
}
