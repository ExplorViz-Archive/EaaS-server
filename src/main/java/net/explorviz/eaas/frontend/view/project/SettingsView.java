package net.explorviz.eaas.frontend.view.project;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.frontend.component.ConfirmDialog;
import net.explorviz.eaas.frontend.layout.ProjectLayout;
import net.explorviz.eaas.frontend.view.MainView;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.repository.ProjectRepository;
import org.springframework.security.access.annotation.Secured;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;
import static com.vaadin.flow.dom.ElementFactory.createParagraph;

@Route(value = "settings", layout = ProjectLayout.class)
@Secured("MANAGE_PROJECT")
@Slf4j
public class SettingsView extends AbstractProjectView {
    private static final long serialVersionUID = -6650300496191931405L;

    private final ProjectRepository projectRepo;

    public SettingsView(ProjectRepository projectRepo) {
        super(projectRepo, "Settings");
        this.projectRepo = projectRepo;
    }

    @Override
    protected void build() {
        getElement().appendChild(createHeading2("Settings"),
            createParagraph("The ID of this project is: " + getProject().getId()));

        // TODO: Change name

        Checkbox hiddenCheckbox = new Checkbox("Hide Project");
        hiddenCheckbox.setValue(getProject().isHidden());
        hiddenCheckbox.addClickListener(this::doSetHidden);
        add(hiddenCheckbox);

        Button deleteButton = new Button("Delete project");
        deleteButton.setIcon(VaadinIcon.FOLDER_REMOVE.create());
        deleteButton.addClickListener(click -> this.doRequestDeletion());
        add(deleteButton);
    }

    private void doSetHidden(ClickEvent<? extends Checkbox> click) {
        getProject().setHidden(click.getSource().getValue());
        this.project = projectRepo.save(getProject());
        Notification.show("Settings saved");
    }

    private void doRequestDeletion() {
        // TODO: Deny deletion if there are running instances

        new ConfirmDialog<>(getProject(), "Really delete project?",
                "Are you sure you want to delete Project '" + getProject().getName() +
                        "'? This action can not be undone. Build images saved in the docker daemon will not be " +
                        "deleted by this action.", this::doDeleteProject).open();
    }

    private void doDeleteProject(Project project) {
        log.info("Deleting project #{} ('{}')", project.getId(), project.getName());
        projectRepo.delete(project);
        getUI().ifPresent(ui -> ui.navigate(MainView.class));
        Notification.show("Deleted project " + project.getName());
    }
}
