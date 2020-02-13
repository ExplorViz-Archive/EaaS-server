package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Common base class for all components that are displayed in a {@link SimpleList}.
 */
@Tag(Tag.LI)
public abstract class SimpleListEntry extends VerticalLayout {
    private static final long serialVersionUID = 2024899118270681235L;

    protected SimpleListEntry() {
        addClassName("simple-list-entry");
    }
}
