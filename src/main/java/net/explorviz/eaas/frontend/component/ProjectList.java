package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.User;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.security.SecurityUtils;
import org.springframework.util.StringUtils;

import java.util.Optional;

public class ProjectList extends VerticalLayout {
    private static final long serialVersionUID = 8287505649008791683L;

    private final TextField projectName;
    private final ProjectRepository projectRepo;

    public ProjectList(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;

        add(new H3("Projects"));

        projectName = new TextField();
        projectName.setMinLength(Project.NAME_MIN_LENGTH);
        projectName.setMaxLength(Project.NAME_MAX_LENGTH);
        projectName.setPlaceholder("Project name");

        Button createProject = new Button("Create new project");
        createProject.addClickListener(click -> this.doCreateProject());

        add(new HorizontalLayout(projectName, createProject));

        projectRepo.findAll().forEach(project -> add(new ProjectListEntry(project, this::doDeleteProject)));
    }

    private void doCreateProject() {
        String name = StringUtils.trimWhitespace(projectName.getValue());
        Optional<User> user = SecurityUtils.getCurrentUser();

        if (StringUtils.isEmpty(name)) {
            projectName.setInvalid(true);
            projectName.setErrorMessage("Project name may not be empty");
        } else if (projectRepo.findByName(name).isPresent()) {
            projectName.setInvalid(true);
            projectName.setErrorMessage("A Project with this name already exists!");
        } else if (user.isEmpty()) {
            throw new IllegalStateException("Tried to create project from unauthenticated context");
        } else {
            projectName.setInvalid(false);
            projectName.setErrorMessage(null);

            Project project = projectRepo.save(new Project(name, user.get()));
            add(new ProjectListEntry(project, this::doDeleteProject));

            Notification.show("Created project " + project.getName());
            projectName.clear();
        }
    }

    private void doDeleteProject(ProjectListEntry entry) {
        projectRepo.delete(entry.getProject());
        remove(entry);
        Notification.show("Deleted project " + entry.getProject().getName());
    }
}
