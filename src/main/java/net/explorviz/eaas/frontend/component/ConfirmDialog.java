package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.function.Consumer;

import static com.vaadin.flow.dom.ElementFactory.createHeading3;

/**
 * Shows a confirmation request to the user. If they confirm, {@code acknowledgeCallback} will be called with the given
 * {@code subject} of type {@code <T>}. The subject has no other purpose.
 */
public class ConfirmDialog<T> extends Dialog {
    private static final long serialVersionUID = 6269645457369540098L;

    public ConfirmDialog(T subject, String title, String message, Consumer<? super T> acknowledgeCallback) {
        HorizontalLayout header = new HorizontalLayout();
        header.setId("dialog-header");
        header.add(VaadinIcon.WARNING.create());
        header.getElement().appendChild(createHeading3(title));
        add(header);

        Paragraph text = new Paragraph(message);
        text.addClassName("dialog-text");
        add(text);

        Button confirmButton = new Button("Confirm", VaadinIcon.CHECK.create());
        confirmButton.setDisableOnClick(true);
        confirmButton.addClickListener(click -> {
            acknowledgeCallback.accept(subject);
            close();
        });
        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.addClassName("dialog-buttons");
        buttonBar.add(confirmButton);
        buttonBar.add(new Button("Abort", VaadinIcon.CLOSE.create(), click -> close()));
        add(buttonBar);
    }
}
