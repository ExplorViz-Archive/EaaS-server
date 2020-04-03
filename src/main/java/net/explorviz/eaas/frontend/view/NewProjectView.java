package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.User;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.security.SecurityUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;

@Route(value = "newproject", layout = MainLayout.class)
@Secured("CREATE_PROJECT")
@PageTitle("New Project - " + Application.PAGE_TITLE)
@Slf4j
public class NewProjectView extends VerticalLayout {
    private static final long serialVersionUID = 177039564703101073L;

    private static final Pattern NAME_PATTERN = Pattern.compile(Project.NAME_PATTERN);

    private final ProjectRepository projectRepo;

    private final TextField projectName;

    public NewProjectView(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;

        getElement().appendChild(createHeading2("New Project"));

        // TODO: Replace form with Binder

        projectName = new TextField();
        projectName.setMinLength(Project.NAME_MIN_LENGTH);
        projectName.setMaxLength(Project.NAME_MAX_LENGTH);
        projectName.setPlaceholder("Project name");

        Button create = new Button("Create");
        create.addClickListener(click -> this.doCreateProject());

        add(new HorizontalLayout(projectName, create));
    }

    private void doCreateProject() {
        User user = SecurityUtils.getCurrentUser().orElseThrow(() ->
            new IllegalStateException("Tried to create project from unauthenticated context"));

        String name = StringUtils.trimWhitespace(projectName.getValue());

        if (!StringUtils.hasText(name)) {
            projectName.setInvalid(true);
            projectName.setErrorMessage("Project name may not be empty");
        } else if (projectRepo.existsByNameIgnoreCase(name)) {
            // TODO: This can be used to probe for existing project names even if they're hidden. Cannot be fixed easily
            projectName.setInvalid(true);
            projectName.setErrorMessage("A project with this name already exists!");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            projectName.setInvalid(true);
            projectName.setErrorMessage("Project name does not match allowed pattern");
        } else {
            projectName.setInvalid(false);
            projectName.setErrorMessage(null);

            Project project = projectRepo.save(new Project(name, user));
            projectName.clear();
            log.info("User #{} ('{}') owns new project #{} ('{}')", user.getId(), user.getUsername(),
                project.getId(), project.getName());
            getUI().ifPresent(ui -> ui.navigate(BuildsView.class, project.getId()));
            Notification.show("Created project " + project.getName());
        }
    }
}
