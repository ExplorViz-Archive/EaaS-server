package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import static com.vaadin.flow.dom.ElementFactory.createHeading3;

/**
 * Shows an informational dialog to the user. It can be closed with a button and does not have any further interaction.
 */
public class InformationDialog extends Dialog {
    private static final long serialVersionUID = 2034322570140951206L;

    public InformationDialog(String title, String message) {
        HorizontalLayout header = new HorizontalLayout();
        header.setId("dialog-header");
        header.add(VaadinIcon.INFO_CIRCLE.create());
        header.getElement().appendChild(createHeading3(title));
        add(header);

        Paragraph text = new Paragraph(message);
        text.addClassName("dialog-text");
        add(text);

        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.addClassName("dialog-buttons");
        buttonBar.add(new Button("Close", VaadinIcon.CLOSE.create(), click -> close()));
        add(buttonBar);
    }
}
