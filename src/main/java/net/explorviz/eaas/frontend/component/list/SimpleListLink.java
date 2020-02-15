package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouterLink;

import static com.vaadin.flow.dom.ElementFactory.createHeading4;

/**
 * Can be used as header in {@link SimpleListEntry} presenting a rich link.
 */
public class SimpleListLink extends RouterLink {
    private static final long serialVersionUID = -4492818211213423908L;

    public <T, C extends Component & HasUrlParameter<T>> SimpleListLink(Class<? extends C> navigationTarget,
                                                                        T parameter, IconFactory icon, String name) {
        addClassName("simple-list-header");

        setRoute(navigationTarget, parameter);
        add(icon.create());
        getElement().appendChild(createHeading4(name));
    }
}
