package net.explorviz.eaas.frontend.view.project;

import com.vaadin.flow.router.Route;
import net.explorviz.eaas.frontend.component.list.SecretListEntry;
import net.explorviz.eaas.frontend.component.list.RichList;
import net.explorviz.eaas.frontend.layout.ProjectLayout;
import net.explorviz.eaas.model.entity.Secret;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.model.repository.SecretRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;

@Route(value = "secrets", layout = ProjectLayout.class)
@Secured("MANAGE_PROJECT")
public class SecretsView extends AbstractProjectView {
    private static final long serialVersionUID = -2777916910494366724L;

    private final SecretRepository secretRepo;

    private RichList<Secret> secretList;

    public SecretsView(ProjectRepository projectRepo, SecretRepository secretRepo) {
        super(projectRepo, "Secrets");
        this.secretRepo = secretRepo;
    }

    @Override
    public void build() {
        getElement().appendChild(createHeading2("Secrets"));

        // TODO: Secret name text field + add button

        Page<Secret> secrets = secretRepo.findByProject(getProject(), Pageable.unpaged());
        secretList = new RichList<>(secret -> new SecretListEntry(secret, this::onDeleteSecret));
        secretList.addEntries(secrets);
        add(secretList);
    }

    private void onDeleteSecret(Secret secret) {
        secretList.removeEntry(secret);
    }
}
