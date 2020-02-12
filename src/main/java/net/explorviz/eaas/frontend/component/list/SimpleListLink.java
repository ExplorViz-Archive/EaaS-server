package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouterLink;

/**
 * Used as header in {@link SimpleListEntry} presenting a rich link.
 */
public class SimpleListLink extends RouterLink {
    private static final long serialVersionUID = -4492818211213423908L;

    public <T, C extends Component & HasUrlParameter<T>> SimpleListLink(Class<? extends C> navigationTarget,
                                                                        T parameter, IconFactory icon, String name) {
        addClassName("simple-list-link");

        setRoute(navigationTarget, parameter);
        add(icon.create());
        add(new H4(name));
    }
}
