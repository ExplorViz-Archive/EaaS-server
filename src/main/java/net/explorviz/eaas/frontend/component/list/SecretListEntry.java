package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.model.entity.Secret;
import net.explorviz.eaas.model.repository.SecretRepository;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.function.Consumer;

import static com.vaadin.flow.dom.ElementFactory.createParagraph;
import static com.vaadin.flow.dom.ElementFactory.createPreformatted;

@Slf4j
public class SecretListEntry extends AbstractListEntry {
    private static final long serialVersionUID = 8271392331540142853L;

    private final Secret secret;
    private final SecretRepository secretRepo;
    private final Consumer<? super Secret> deleteCallback;

    public SecretListEntry(Secret secret, SecretRepository secretRepo, Consumer<? super Secret> deleteCallback) {
        this.secret = secret;
        this.secretRepo = secretRepo;
        this.deleteCallback = deleteCallback;

        ZonedDateTime lastUse = secret.getLastUsedDate();
        Element lastUseElement;
        if (lastUse == null) {
            lastUseElement = createParagraph("Never used");
        } else {
            lastUseElement = createParagraph("Last used " + lastUse.format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)));
        }

        add(RichHeader.create(VaadinIcon.KEY.create(), secret.getName()));

        // TODO: Replace secret with asterisks by default, add unveil/copy to clipboard buttons

        getElement().appendChild(
            createParagraph("Created " + secret.getCreatedDate().format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
            )),
            lastUseElement,
            createPreformatted(secret.getSecret())
        );

        Button deleteButton = new Button("Delete", click -> this.onDelete());
        deleteButton.setIcon(VaadinIcon.TRASH.create());
        // VerticalLayout is to enforce newline after previous Preformatted element
        add(new VerticalLayout(deleteButton));
    }

    private void onDelete() {
        log.info("Deleting secret #{} ('{}') of project #{} ('{}') ", secret.getId(), secret.getName(),
            secret.getProject().getId(), secret.getProject().getName());
        secretRepo.delete(secret);
        Notification.show("Deleted secret " + secret.getName());
        deleteCallback.accept(secret);
    }
}
