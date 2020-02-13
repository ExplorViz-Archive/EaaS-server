package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.IconFactory;

/**
 * A link ({@link Anchor}) with an icon next to its label. Icons always have the same predefined size.
 */
public class IconAnchor extends Anchor {
    private static final long serialVersionUID = -6463742407143880609L;

    protected IconAnchor(String href, Component... components) {
        super(href, components);
        addClassName("icon-anchor");
    }

    /**
     * Create an IconAnchor displaying the given VaadinIcon next to the text.
     */
    public static IconAnchor createFromIcon(String href, IconFactory icon, String text) {
        return new IconAnchor(href, icon.create(), new Text(text));
    }

    /**
     * Create an IconAnchor displaying the given image file next to the text.
     */
    public static IconAnchor createFromImage(String href, String imageSrc, String text) {
        return new IconAnchor(href, new Image(imageSrc, text), new Text(text));
    }
}
