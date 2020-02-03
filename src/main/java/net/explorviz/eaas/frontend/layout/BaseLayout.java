package net.explorviz.eaas.frontend.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouterLink;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.view.DynamicView;
import net.explorviz.eaas.security.SecurityUtils;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>Note for child classes:</b> All tab entries have to be added in {@link #build()} so they are available when this
 * class decided which tab is marked as selected. Also layouts can be reused by the framework, so the tab entries need
 * to be rebuilt whenever the view changes.
 */
public abstract class BaseLayout extends AppLayout implements BeforeEnterObserver {
    private static final long serialVersionUID = 6416207502947013549L;

    private final Tabs tabs = new Tabs();
    private final Map<Class<? extends Component>, Tab> tabTargets = new HashMap<>(6);

    protected BaseLayout() {
        // Toggle button for the sidebar
        addToNavbar(new DrawerToggle());

        // Branding in the header bar
        Image img = new Image("/icons/icon-192x192.png", "ExplorViz");
        img.setHeight("64px");
        img.setWidth("64px");
        HorizontalLayout branding = new HorizontalLayout(img, new H2(Application.PAGE_TITLE));
        branding.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        addToNavbar(branding);

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        addToDrawer(tabs);
    }

    /**
     * Clears all tabs. It is necessary to call this every time before a child class adds their tabs because Layouts are
     * reused across multiple views (that means {@link #beforeEnter(BeforeEnterEvent)} is called on the same Layout
     * object every time the view changes!) and otherwise existing tabs would be duplicated.
     */
    private void resetTabs() {
        tabs.removeAll();
        tabTargets.clear();
    }

    /**
     * Start a new navigation section in the sidebar, separated by a heading label.
     *
     * @param label User-visible section label to display
     */
    protected void startSection(@NonNull String label) {
        // TODO: Navigation sections
        //tabs.add(new H3(label));
    }

    // TODO: Icons

    /**
     * @see #addNavigationTab(String, Class, Object)
     */
    protected void addNavigationTab(@NonNull String label, @NonNull Class<? extends Component> navigationTarget) {
        addNavigationTab(new RouterLink(label, navigationTarget), navigationTarget);
    }

    /**
     * Add a navigation menu entry to the sidebar.
     *
     * @param label            User-visible menu entry label
     * @param navigationTarget View that will be entered when clicking the tab
     * @param parameter        Parameter for the view, which needs to be a {@link HasUrlParameter}
     */
    protected <T, C extends Component & HasUrlParameter<T>>
    void addNavigationTab(@NonNull String label, @NonNull Class<? extends C> navigationTarget, @NonNull T parameter) {
        addNavigationTab(new RouterLink(label, navigationTarget, parameter), navigationTarget);
    }

    private void addNavigationTab(RouterLink link, Class<? extends Component> target) {
        if (SecurityUtils.mayAccess(target)) {
            Tab tab = new Tab(link);
            tabTargets.put(target, tab);
            tabs.add(tab);
        }
    }

    /**
     * Clears all tabs, calls {@link #build()} so child classes can add all the tabs, then sets the currently selected
     * tab based on the view the user is about to enter.
     * <p>
     * Child classes who override this method because they need to access {@link BeforeEnterEvent} must remember to call
     * this method at the end.
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        resetTabs();
        build();

        tabs.setSelectedTab(tabTargets.get(event.getNavigationTarget()));
    }

    /**
     * @see DynamicView#build()
     */
    protected abstract void build();
}
