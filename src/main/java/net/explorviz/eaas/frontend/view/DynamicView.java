package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;

/**
 * Common base class for all views with dynamic content that needs to be updated whenever the user reenters the view.
 */
public abstract class DynamicView extends VerticalLayout implements BeforeEnterObserver {
    private static final long serialVersionUID = 2306625676607673378L;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();
        build();
    }

    /**
     * Add all components to the view. This method is called from {@link #beforeEnter(BeforeEnterEvent)}, which means
     * all dependency injection and {@link com.vaadin.flow.router.HasUrlParameter#setParameter(BeforeEvent, Object)} has
     * been called before.
     * <p>
     * It is necessary to add components in this method only, as Views can be reused by the framework and contents might
     * not update without rebuilding the view upon reentering.
     */
    protected abstract void build();
}
