package net.explorviz.eaas.frontend.view.project;

import com.vaadin.flow.router.Route;
import net.explorviz.eaas.frontend.layout.ProjectLayout;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.model.repository.SecretRepository;
import org.springframework.security.access.annotation.Secured;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;

@Route(value = "secrets", layout = ProjectLayout.class)
@Secured("MANAGE_PROJECT")
public class SecretsView extends ProjectView {
    private static final long serialVersionUID = -2777916910494366724L;

    private final SecretRepository secretRepo;

    public SecretsView(ProjectRepository projectRepo, SecretRepository secretRepo) {
        super(projectRepo, "Secrets");
        this.secretRepo = secretRepo;
    }

    @Override
    public void build() {
        getElement().appendChild(createHeading2("Secrets"));

        // TODO: Secrets view
    }
}
