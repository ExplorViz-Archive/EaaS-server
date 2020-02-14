package net.explorviz.eaas.frontend.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.frontend.component.ExplorVizBanner;
import net.explorviz.eaas.frontend.layout.component.NavbarActions;
import net.explorviz.eaas.frontend.layout.component.NavigationTab;
import net.explorviz.eaas.security.SecurityUtils;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * <b>Note for child classes:</b> All tab entries have to be added in {@link #build()} so they are available when this
 * class decides which tab is marked as selected.
 */
@Slf4j
@CssImport("./style/layout.css")
public abstract class BaseLayout extends AppLayout implements BeforeEnterObserver {
    private static final long serialVersionUID = 6416207502947013549L;

    private final Collection<Tabs> sections = new ArrayList<>(3);
    private final VerticalLayout navigation = new VerticalLayout();
    private final Map<Class<? extends Component>, Tab> tabTargets = new HashMap<>(8);

    private boolean built;

    private String currentLabel;
    private Tabs currentSection;

    protected BaseLayout() {
        addToNavbar(new DrawerToggle());
        addToNavbar(new ExplorVizBanner(false));
        addToNavbar(new NavbarActions());

        navigation.setId("navigation-panel");
        addToDrawer(navigation);
    }

    /**
     * Clears all tabs. This is necessary if we want to change the available tabs dynamically, because Layouts are
     * reused across multiple views (which means {@link #beforeEnter(BeforeEnterEvent)} is called on the same Layout
     * object every time the view changes!) and otherwise existing tabs would be duplicated.
     * <p>
     * After calling this method, {@link #build()} is called again when {@link #beforeEnter(BeforeEnterEvent)} runs the
     * next time.
     */
    protected void resetTabs() {
        sections.clear();
        navigation.removeAll();
        tabTargets.clear();
        built = false;
    }

    /**
     * Start a new navigation section in the sidebar, separated by a heading label. Sections are only displayed if they
     * contain at least one tab.
     *
     * @param label User-visible section header to display or {@code null} to not display a section header
     */
    protected void startSection(@Nullable String label) {
        currentSection = new Tabs();
        currentSection.setOrientation(Tabs.Orientation.VERTICAL);

        // The section is added to the sidebar when a navigation tab is added, see below

        currentLabel = label;
    }

    /**
     * Add a tab to the current section if the current security context is authorized to access the navigation target
     * according to {@link SecurityUtils#mayAccess(Class)}.
     * <p>
     * If {@link #startSection(String)} wasn't called since the last {@link #resetTabs()}, a default section without a
     * label will be used.
     */
    protected void addNavigationTab(NavigationTab tab) {
        Class<? extends Component> target = tab.getNavigationTarget();

        if (SecurityUtils.mayAccess(target)) {
            if (!sections.contains(currentSection)) {
                // Now that we know we have at least one tab to display we can add the section to the sidebar
                sections.add(currentSection);

                if (currentLabel != null) {
                    navigation.add(new H4(currentLabel));
                }
                navigation.add(currentSection);
            }

            tabTargets.put(target, tab);
            currentSection.add(tab);
        }
    }

    /**
     * Calls {@link #build()} so child classes can add all the tabs, then sets the currently selected tab based on the
     * view the user is about to enter.
     * <p>
     * Child classes who override this method because they need to access {@link BeforeEnterEvent} must remember to call
     * this method at the end.
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!built) {
            log.debug("(Re)building layout {}", this.getClass().getCanonicalName());

            startSection(null);
            build();
            built = true;
        }

        // Select the tab for the entered view. Be aware we do not respect parameters here (we don't need it yet)
        Tab selectedTab = tabTargets.get(event.getNavigationTarget());
        sections.forEach(section -> {
            if (section.getChildren().anyMatch(child -> child == selectedTab)) {
                section.setSelectedTab(selectedTab);
            } else {
                section.setSelectedIndex(-1);
            }
        });
    }

    /**
     * Add all tabs and sections to the navigation. This method is called from {@link #beforeEnter(BeforeEnterEvent)},
     * which means all dependency injection has happened before. All tabs have to be made available from this method.
     */
    protected abstract void build();
}
