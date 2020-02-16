package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouterLink;

import static com.vaadin.flow.dom.ElementFactory.createHeading4;

/**
 * Can be used as header in {@link AbstractListEntry} presenting a rich link.
 */
public class RichLinkHeader extends RouterLink {
    private static final long serialVersionUID = -4492818211213423908L;

    public <T, C extends Component & HasUrlParameter<T>> RichLinkHeader(Class<? extends C> navigationTarget,
                                                                        T parameter, Icon icon, String name) {
        addClassName("simple-list-header");

        setRoute(navigationTarget, parameter);
        add(icon);
        getElement().appendChild(createHeading4(name));
    }
}
