package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class SimpleListEntry extends VerticalLayout {
    private static final long serialVersionUID = 2024899118270681235L;

    protected SimpleListEntry() {
        addClassName("simple-list-entry");
    }
}
