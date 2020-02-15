package net.explorviz.eaas.frontend.view.project;

import com.vaadin.flow.router.Route;
import net.explorviz.eaas.frontend.layout.ProjectLayout;
import net.explorviz.eaas.model.repository.ProjectRepository;
import org.springframework.security.access.annotation.Secured;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;

@Route(value = "settings", layout = ProjectLayout.class)
@Secured("MANAGE_PROJECT")
public class SettingsView extends AbstractProjectView {
    private static final long serialVersionUID = -6650300496191931405L;

    public SettingsView(ProjectRepository projectRepo) {
        super(projectRepo, "Settings");
    }

    @Override
    protected void build() {
        getElement().appendChild(createHeading2("Settings"));

        // TODO: Settings view
    }
}
