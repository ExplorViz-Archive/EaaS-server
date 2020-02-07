package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * A link ({@link Anchor}) with an icon next to its label. Icons always have the same predefined size.
 */
public class IconAnchor extends Anchor {
    private static final long serialVersionUID = -6463742407143880609L;

    private static final String ICON_SIZE = "24px";

    protected IconAnchor(String href, Component... components) {
        super(href, components);
        addClassName("icon-anchor");
    }

    /**
     * Create an IconAnchor displaying the given VaadinIcon next to the text.
     */
    public static IconAnchor createFromIcon(String href, VaadinIcon vaadinIcon, String text) {
        Icon icon = new Icon(vaadinIcon);
        icon.setSize(ICON_SIZE);
        return new IconAnchor(href, icon, new Text(text));
    }

    /**
     * Create an IconAnchor displaying the given image file next to the text.
     */
    public static IconAnchor createFromImage(String href, String imageSrc, String text) {
        Image image = new Image(imageSrc, "ExplorViz");
        image.setWidth(ICON_SIZE);
        image.setHeight(ICON_SIZE);
        return new IconAnchor(href, image, new Text(text));
    }
}
