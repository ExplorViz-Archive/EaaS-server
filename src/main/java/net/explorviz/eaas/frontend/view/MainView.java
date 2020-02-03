package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.frontend.component.ProjectList;

@PageTitle(Application.PAGE_TITLE)
@Route(value = "", layout = MainLayout.class)
public class MainView extends DynamicView {
    private static final long serialVersionUID = -4417018497155730464L;

    private final ProjectRepository projectRepo;

    public MainView(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
    }

    @Override
    protected void build() {
        add(new H2("Projects"));

        add(new ProjectList(projectRepo));
    }
}
