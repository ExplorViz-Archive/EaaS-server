package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.component.RecentlyUpdatedProjectListEntry;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.frontend.component.SimpleList;
import net.explorviz.eaas.model.repository.RecentlyUpdatedResult;
import net.explorviz.eaas.security.SecurityUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@PageTitle(Application.PAGE_TITLE)
@Route(value = "", layout = MainLayout.class)
public class MainView extends DynamicView {
    private static final long serialVersionUID = -4417018497155730464L;

    private final ProjectRepository projectRepo;
    private final int projectsPerPage;

    public MainView(ProjectRepository projectRepo,
                    @Value("${eaas.paging.home.projects:5}") int projectsPerPage) {
        Validate.inclusiveBetween(1, Integer.MAX_VALUE, projectsPerPage, "projects must be at least 1");

        this.projectRepo = projectRepo;
        this.projectsPerPage = projectsPerPage;
    }

    @Override
    protected void build() {
        add(new H2("Recently updated projects"));

        Page<RecentlyUpdatedResult> projects = projectRepo.findRecentlyUpdated(false,
            SecurityUtils.getCurrentUser().orElse(null), PageRequest.of(0, projectsPerPage));

        if (projects.getTotalElements() == 0) {
            add(new Paragraph("No projects have been created yet."));
        } else {
            add(new SimpleList<>(projects, RecentlyUpdatedProjectListEntry::new));
        }
    }

    //private void doDeleteProject(RecentlyUpdatedProjectListEntry entry) {
    //    projectRepo.delete(entry.getProject());
    //    remove(entry);
    //    Notification.show("Deleted project " + entry.getProject().getName());
    //}
}
