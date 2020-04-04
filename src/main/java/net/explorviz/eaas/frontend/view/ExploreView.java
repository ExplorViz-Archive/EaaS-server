package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.component.list.ProjectListEntry;
import net.explorviz.eaas.frontend.component.list.RichList;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.security.SecurityUtils;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;
import static com.vaadin.flow.dom.ElementFactory.createParagraph;

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
        getElement().appendChild(createHeading2("All projects"));

        // TODO: Paging
        List<Project> projects =
            projectRepo.findAll(Pageable.unpaged()).stream()
                .filter(project -> !project.isHidden() || SecurityUtils.hasReadAccess(project))
                .collect(Collectors.toList());

        if (projects.isEmpty()) {
            getElement().appendChild(createParagraph("No projects have been created yet."));
        } else {
            RichList<Project> projectList = new RichList<>(ProjectListEntry::new);
            projectList.addEntries(projects);
            add(projectList);
        }
    }
}
