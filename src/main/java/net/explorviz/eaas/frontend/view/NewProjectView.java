package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Route(value = "newproject", layout = MainLayout.class)
@Secured("CREATE_PROJECT")
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

        add(new H2("New project"));

        projectName = new TextField();
        projectName.setMinLength(Project.NAME_MIN_LENGTH);
        projectName.setMaxLength(Project.NAME_MAX_LENGTH);
        projectName.setPlaceholder("Project name");

        Button createProject = new Button("Create new project");
        createProject.addClickListener(click -> this.doCreateProject());

        add(new HorizontalLayout(projectName, createProject));
    }

    private void doCreateProject() {
        Optional<User> user = SecurityUtils.getCurrentUser();
        if (user.isEmpty()) {
            throw new IllegalStateException("Tried to create project from unauthenticated context");
        }

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

            Project project = projectRepo.save(new Project(name, user.get()));
            projectName.clear();

            // TODO: Remove dummy secret generation during development
            Secret secret = secretRepo.save(new Secret("The Secret", keyGenerator.generateAPIKey(), project));
            log.info("Secret for new project {} is {}", project.getName(), secret.getSecret());

            UI.getCurrent().navigate(BuildsView.class, project.getId());
            Notification.show("Created project " + project.getName());
        }
    }
}
