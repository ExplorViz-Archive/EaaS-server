package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouterLink;
import lombok.Getter;

import static com.vaadin.flow.dom.ElementFactory.createHeading4;

/**
 * To be used as header in {@link AbstractListEntry}.
 */
public final class RichHeader extends HorizontalLayout {
    private static final long serialVersionUID = -7275699618363477465L;

    @Getter
    private final Icon icon;

    private RichHeader(Icon icon) {
        this.icon = icon;

        addClassName("rich-list-header");
    }

    /**
     * Create a new header with an icon and a heading.
     */
    public static RichHeader create(Icon icon, String name) {
        RichHeader header = new RichHeader(icon);
        header.add(icon);
        header.getElement().appendChild(createHeading4(name));
        return header;
    }

    /**
     * Create a new header with an icon and a heading, both are clickable to navigate to the specified view.
     */
    public static <T, C extends Component & HasUrlParameter<T>>
        RichHeader createWithLink(Class<? extends C> navigationTarget, T parameter, Icon icon, String name) {
        RouterLink link = new RouterLink();
        link.setRoute(navigationTarget, parameter);
        link.add(icon);
        link.getElement().appendChild(createHeading4(name));

        RichHeader header = new RichHeader(icon);
        header.add(link);
        return header;
    }
}
