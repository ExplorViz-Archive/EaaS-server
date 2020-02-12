package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.component.ProjectListEntry;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.User;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.frontend.component.ProjectList;
import net.explorviz.eaas.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@PageTitle(Application.PAGE_TITLE)
@Route(value = "", layout = MainLayout.class)
public class MainView extends DynamicView {
    private static final long serialVersionUID = -4417018497155730464L;

    private final ProjectRepository projectRepo;
    private final int projectsPerPage;

    public MainView(ProjectRepository projectRepo,
                    @Value("${eaas.paging.home.projects:5}") int projectsPerPage) {
        this.projectRepo = projectRepo;
        this.projectsPerPage = projectsPerPage;
    }

    @Override
    protected void build() {
        add(new H2("Projects"));

        Pageable page = PageRequest.of(0, projectsPerPage, Sort.Direction.DESC, "lastModifiedDate");

        Optional<User> user = SecurityUtils.getCurrentUser();
        Iterable<Project> projects;
        if (user.isPresent()) {
            projects = projectRepo.findByHiddenOrOwner(false, user.get(), page);
        } else {
            projects = projectRepo.findByHidden(false, page);
        }

        add(new ProjectList(projects));
    }

    private void doDeleteProject(ProjectListEntry entry) {
        projectRepo.delete(entry.getProject());
        remove(entry);
        Notification.show("Deleted project " + entry.getProject().getName());
    }
}
