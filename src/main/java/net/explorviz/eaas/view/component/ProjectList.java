package net.explorviz.eaas.view.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import net.explorviz.eaas.model.Project;
import net.explorviz.eaas.repository.ProjectRepository;
import org.apache.commons.lang3.StringUtils;

public class ProjectList extends VerticalLayout {
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
        createProject.addClickListener(this::doCreateProject);

        add(new HorizontalLayout(projectName, createProject));

        projectRepo.findAll().forEach(project -> add(new ProjectListEntry(project, this::doDeleteProject)));
    }

    private void doCreateProject(ClickEvent<Button> click) {
        String name = StringUtils.trim(projectName.getValue());
        if (StringUtils.isBlank(name)) {
            projectName.setInvalid(true);
            projectName.setErrorMessage("Project name may not be empty");
        } else if (projectRepo.findByName(name).isPresent()) {
            projectName.setInvalid(true);
            projectName.setErrorMessage("A Project with this name already exists!");
        } else {
            projectName.setInvalid(false);
            projectName.setErrorMessage(null);

            Project project = projectRepo.save(new Project(name, null));
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
