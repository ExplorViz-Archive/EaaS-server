package net.explorviz.eaas.frontend.view.project;

import com.vaadin.flow.router.*;
import lombok.AccessLevel;
import lombok.Getter;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.view.DynamicView;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.repository.ProjectRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;

import java.util.Optional;

/**
 * Common base class for all views specific to a project. Routes are expected to have a the project ID as trailing
 * parameter and should use the {@link net.explorviz.eaas.frontend.layout.ProjectLayout}.
 * <p>
 * Components need to be added in the {@link #build()} method only, as the project is only available after {@link
 * #setParameter(BeforeEvent, Long)} has been called.
 */
@Secured("READ_PROJECT")
public abstract class ProjectView extends DynamicView implements HasUrlParameter<Long>, HasDynamicTitle {
    private static final long serialVersionUID = 8034796492440190988L;

    private final ProjectRepository projectRepo;
    private final String subPageTitle;

    @Getter(AccessLevel.PROTECTED)
    private Project project;

    protected ProjectView(ProjectRepository projectRepo, @NonNull String subPageTitle) {
        this.projectRepo = projectRepo;
        this.subPageTitle = subPageTitle;
    }

    @Override
    public void setParameter(BeforeEvent event, Long projectId) {
        Optional<Project> optProject = projectRepo.findById(projectId);
        if (optProject.isEmpty()) {
            event.rerouteToError(NotFoundException.class, "Project not found");
            return;
        }
        project = optProject.get();
    }

    // TODO: Only allow access to non-hidden or owned projects

    @Override
    public String getPageTitle() {
        return subPageTitle + " - " + project.getName() + " - " + Application.PAGE_TITLE;
    }
}
