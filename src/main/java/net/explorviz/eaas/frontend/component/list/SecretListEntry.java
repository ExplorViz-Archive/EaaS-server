package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Element;
import net.explorviz.eaas.model.entity.Secret;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.function.Consumer;

import static com.vaadin.flow.dom.ElementFactory.createParagraph;
import static com.vaadin.flow.dom.ElementFactory.createPreformatted;

public class SecretListEntry extends AbstractListEntry {
    private static final long serialVersionUID = 8271392331540142853L;

    public SecretListEntry(Secret secret, Consumer<? super Secret> deleteCallback) {
        ZonedDateTime lastUse = secret.getLastUsedDate();
        Element lastUseElement;
        if (lastUse == null) {
            lastUseElement = createParagraph("Never used");
        } else {
            lastUseElement = createParagraph("Last used " + lastUse.format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)));
        }

        add(RichHeader.create(VaadinIcon.KEY.create(), secret.getName()));

        getElement().appendChild(
            createParagraph("Created " + secret.getCreatedDate().format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
            )),
            lastUseElement,
            createPreformatted(secret.getSecret())
        );

        // TODO: Delete button
    }
}
