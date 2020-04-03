package net.explorviz.eaas.frontend.view.project;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.frontend.component.ConfirmDialog;
import net.explorviz.eaas.frontend.layout.ProjectLayout;
import net.explorviz.eaas.frontend.view.MainView;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;
import static com.vaadin.flow.dom.ElementFactory.createParagraph;

@Route(value = "settings", layout = ProjectLayout.class)
@Slf4j
public class SettingsView extends AbstractProjectView {
    private static final long serialVersionUID = -6650300496191931405L;

    private final ProjectRepository projectRepo;

    public SettingsView(ProjectRepository projectRepo) {
        super(projectRepo, "Settings");
        this.projectRepo = projectRepo;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        assert getProject() != null;

        if (!SecurityUtils.hasManageAccess(getProject())) {
            throw new AccessDeniedException("You do not have permission to access this page.");
        }

        super.beforeEnter(event);
    }

    @Override
    protected void build() {
        getElement().appendChild(createHeading2("Settings"),
            createParagraph("The ID of this project is: " + getProject().getId()));

        // TODO: Add change name function

        Checkbox hiddenCheckbox = new Checkbox("Hide project from public");
        hiddenCheckbox.setValue(getProject().isHidden());
        hiddenCheckbox.addValueChangeListener(this::onHiddenChanged);
        add(hiddenCheckbox);

        Button deleteButton = new Button("Delete project");
        deleteButton.setIcon(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(click -> this.doRequestDeletion());
        add(deleteButton);
    }

    private void onHiddenChanged(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
        getProject().setHidden(event.getValue());
        setProject(projectRepo.save(getProject()));
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
