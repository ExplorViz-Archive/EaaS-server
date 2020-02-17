package net.explorviz.eaas.frontend.layout.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouterLink;
import lombok.Getter;
import org.springframework.lang.NonNull;

import static com.vaadin.flow.dom.Element.createText;

public final class NavigationTab extends Tab {
    private static final long serialVersionUID = 188662863223269797L;

    @Getter
    private final Class<? extends Component> navigationTarget;

    private NavigationTab(RouterLink routerLink, String label, Icon icon,
                            Class<? extends Component> navigationTarget) {
        this.navigationTarget = navigationTarget;

        addClassName("navigation-tab");

        routerLink.add(icon);
        routerLink.getElement().appendChild(createText(label));
        add(routerLink);
    }

    /**
     * @see #createWithParameter(String, Icon, Class, Object)
     */
    public static NavigationTab create(@NonNull String label, @NonNull Icon icon,
                                       @NonNull Class<? extends Component> navigationTarget) {
        RouterLink link = new RouterLink();
        link.setRoute(navigationTarget);
        return new NavigationTab(link, label, icon, navigationTarget);
    }

    /**
     * Add a navigation menu entry to the sidebar.
     *
     * @param label            User-visible label
     * @param icon             Icon to display next to the text label
     * @param navigationTarget View that will be entered when clicking the tab
     * @param parameter        Parameter for the view, which needs to be a {@link HasUrlParameter}
     */
    public static <T, C extends Component & HasUrlParameter<T>>
        NavigationTab createWithParameter(@NonNull String label, @NonNull Icon icon,
                                          @NonNull Class<? extends C> navigationTarget, @NonNull T parameter) {
        RouterLink link = new RouterLink();
        link.setRoute(navigationTarget, parameter);
        return new NavigationTab(link, label, icon, navigationTarget);
    }
}
