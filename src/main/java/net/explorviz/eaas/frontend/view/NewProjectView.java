package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.Secret;
import net.explorviz.eaas.model.entity.User;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.model.repository.SecretRepository;
import net.explorviz.eaas.security.KeyGenerator;
import net.explorviz.eaas.security.SecurityUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;

@Route(value = "newproject", layout = MainLayout.class)
@Secured("CREATE_PROJECT")
@PageTitle("New Project - " + Application.PAGE_TITLE)
public class NewProjectView extends VerticalLayout {
    private static final long serialVersionUID = 177039564703101073L;

    private static final Pattern NAME_PATTERN = Pattern.compile(Project.NAME_PATTERN);

    private final ProjectRepository projectRepo;
    private final SecretRepository secretRepo;
    private final KeyGenerator keyGenerator;

    private final TextField projectName;

    public NewProjectView(ProjectRepository projectRepo, SecretRepository secretRepo, KeyGenerator keyGenerator) {
        this.projectRepo = projectRepo;
        this.secretRepo = secretRepo;
        this.keyGenerator = keyGenerator;

        getElement().appendChild(createHeading2("New Project"));

        projectName = new TextField();
        projectName.setMinLength(Project.NAME_MIN_LENGTH);
        projectName.setMaxLength(Project.NAME_MAX_LENGTH);
        projectName.setPlaceholder("Project name");

        Button createProject = new Button("Create");
        createProject.addClickListener(click -> this.doCreateProject());

        add(new HorizontalLayout(projectName, createProject));
    }

    private void doCreateProject() {
        User user = SecurityUtils.getCurrentUser().orElseThrow(() ->
            new IllegalStateException("Tried to create project from unauthenticated context"));

        // TODO: Use Binder with proper validator

        String name = StringUtils.trimWhitespace(projectName.getValue());

        if (!StringUtils.hasText(name)) {
            projectName.setInvalid(true);
            projectName.setErrorMessage("Project name may not be empty");
        } else if (projectRepo.findByName(name).isPresent()) {
            projectName.setInvalid(true);
            projectName.setErrorMessage("A project with this name already exists!");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            projectName.setInvalid(true);
            projectName.setErrorMessage("Project name contains forbidden characters");
        } else {
            projectName.setInvalid(false);
            projectName.setErrorMessage(null);

            Project project = projectRepo.save(new Project(name, user));
            projectName.clear();

            // TODO: Remove dummy secret generation after initial development phase
            secretRepo.save(new Secret("The Secret", keyGenerator.generateAPIKey(), project));

            getUI().ifPresent(ui -> ui.navigate(BuildsView.class, project.getId()));
            Notification.show("Created project " + project.getName());
        }
    }
}
