package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import net.explorviz.eaas.frontend.component.list.SimpleListEntry;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;

import java.util.function.Consumer;

/**
 * A horizontal bar with control buttons for an ExplorVizInstance, best used within a {@link SimpleListEntry}.
 */
public class ExplorVizControls extends HorizontalLayout {
    private static final long serialVersionUID = 7716806751192597483L;

    public ExplorVizControls(ExplorVizInstance instance, Consumer<? super ExplorVizInstance> stopListener) {
        addClassName("instance-controls");

        Anchor link = IconAnchor.createFromImage(instance.getAccessURL(), "icons/icon-48x48.png", "Open");
        link.setTarget("_blank");
        add(link);

        Button stopButton = new Button("Stop");
        stopButton.addClickListener(click -> stopListener.accept(instance));
        stopButton.setIcon(VaadinIcon.CLOSE_SMALL.create());
        add(stopButton);
    }
}
