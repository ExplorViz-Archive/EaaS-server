package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;

import java.util.function.Consumer;

public class ExplorVizListEntry extends VerticalLayout {
    private static final long serialVersionUID = -1493130701688498902L;

    @Getter
    private final ExplorVizInstance instance;

    public ExplorVizListEntry(ExplorVizInstance instance, Consumer<? super ExplorVizListEntry> stopListener) {
        this.instance = instance;

        add(new H4(instance.getName()));
        add(new Paragraph("Running ExplorViz " + instance.getVersion() + ", started on " + instance.getCreatedTime() +
            ", for build #" + instance.getBuildId() + " (image " + instance.getApplicationImage() + ")"));

        HorizontalLayout controls = new HorizontalLayout();

        Anchor link = new Anchor(instance.getAccessURL(), "Open");
        link.setTarget("_blank");
        controls.add(link);

        Button deleteButton = new Button("Stop");
        deleteButton.addClickListener(click -> stopListener.accept(this));
        controls.add(deleteButton);

        add(controls);
    }
}
