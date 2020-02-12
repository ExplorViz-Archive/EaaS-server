package net.explorviz.eaas.frontend.layout.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouterLink;
import lombok.Getter;
import org.springframework.lang.NonNull;

public class NavigationTab extends Tab {
    private static final long serialVersionUID = 188662863223269797L;

    @Getter
    private final Class<? extends Component> navigationTarget;

    protected NavigationTab(RouterLink routerLink, String label, IconFactory vaadinIcon,
                            Class<? extends Component> navigationTarget) {
        this.navigationTarget = navigationTarget;

        addClassName("navigation-tab");

        routerLink.add(vaadinIcon.create());
        routerLink.add(new Text(label));
        add(routerLink);
    }

    /**
     * @see #createWithParameter(String, VaadinIcon, Class, Object)
     */
    public static NavigationTab create(@NonNull String label, @NonNull VaadinIcon vaadinIcon,
                                       @NonNull Class<? extends Component> navigationTarget) {
        RouterLink link = new RouterLink();
        link.setRoute(navigationTarget);
        return new NavigationTab(link, label, vaadinIcon, navigationTarget);
    }

    /**
     * Add a navigation menu entry to the sidebar.
     *
     * @param label            User-visible label
     * @param vaadinIcon       Icon to display next to the text label
     * @param navigationTarget View that will be entered when clicking the tab
     * @param parameter        Parameter for the view, which needs to be a {@link HasUrlParameter}
     */
    public static <T, C extends Component & HasUrlParameter<T>>
        NavigationTab createWithParameter(@NonNull String label, @NonNull VaadinIcon vaadinIcon,
                                          @NonNull Class<? extends C> navigationTarget, @NonNull T parameter) {
        RouterLink link = new RouterLink();
        link.setRoute(navigationTarget, parameter);
        return new NavigationTab(link, label, vaadinIcon, navigationTarget);
    }
}
