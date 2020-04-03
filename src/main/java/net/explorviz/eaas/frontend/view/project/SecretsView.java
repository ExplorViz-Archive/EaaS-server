package net.explorviz.eaas.frontend.view.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.frontend.component.list.RichList;
import net.explorviz.eaas.frontend.component.list.SecretListEntry;
import net.explorviz.eaas.frontend.layout.ProjectLayout;
import net.explorviz.eaas.model.entity.Secret;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.model.repository.SecretRepository;
import net.explorviz.eaas.security.KeyGenerator;
import net.explorviz.eaas.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;
import static com.vaadin.flow.dom.ElementFactory.createHeading4;

@Route(value = "secrets", layout = ProjectLayout.class)
@Slf4j
public class SecretsView extends AbstractProjectView {
    private static final long serialVersionUID = -2777916910494366724L;

    private final SecretRepository secretRepo;
    private final KeyGenerator keyGenerator;

    private RichList<Secret> secretList;
    private TextField secretName;

    public SecretsView(ProjectRepository projectRepo, SecretRepository secretRepo, KeyGenerator keyGenerator) {
        super(projectRepo, "Secrets");
        this.secretRepo = secretRepo;
        this.keyGenerator = keyGenerator;
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
    public void build() {
        getElement().appendChild(createHeading2("Secrets"), createHeading4("Create new secret"));

        // TODO: Replace form with binder

        secretName = new TextField();
        secretName.setMinLength(Secret.NAME_MIN_LENGTH);
        secretName.setMaxLength(Secret.NAME_MAX_LENGTH);
        secretName.setPlaceholder("Secret name");

        Button create = new Button("Create");
        create.addClickListener(click -> this.doCreateSecret());

        add(new HorizontalLayout(secretName, create));

        Page<Secret> secrets = secretRepo.findByProject(getProject(), Pageable.unpaged());

        secretList = new RichList<>(secret -> new SecretListEntry(secret, secretRepo, secretList::removeEntry));
        secretList.addEntries(secrets);
        add(secretList);
    }

    private void doCreateSecret() {
        String name = StringUtils.trimWhitespace(secretName.getValue());

        if (!StringUtils.hasText(name)) {
            secretName.setInvalid(true);
            secretName.setErrorMessage("Secret name may not be empty");
        } else if (secretRepo.existsByProjectAndNameIgnoreCase(getProject(), name)) {
            secretName.setInvalid(true);
            secretName.setErrorMessage("A secret with this name already exists!");
        } else {
            secretName.setInvalid(false);
            secretName.setErrorMessage(null);

            Secret secret = secretRepo.save(new Secret(name, keyGenerator.generateAPIKey(), getProject()));
            secretName.clear();
            log.info("Created new secret #{} ('{}') for project #{} ('{}')", secret.getId(), secret.getName(),
                secret.getProject().getId(), secret.getProject().getName());
            secretList.addEntry(secret);
            Notification.show("Created secret " + secret.getName());
        }
    }
}
