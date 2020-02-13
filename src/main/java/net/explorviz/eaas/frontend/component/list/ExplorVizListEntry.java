package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import net.explorviz.eaas.frontend.component.ExplorVizControls;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.function.Consumer;

public class ExplorVizListEntry extends SimpleListEntry {
    private static final long serialVersionUID = -1493130701688498902L;

    public ExplorVizListEntry(ExplorVizInstance instance, Consumer<? super ExplorVizInstance> stopListener) {
        add(new H4(instance.getName()));
        add(new Paragraph("ExplorViz " + instance.getVersion() + ", started " + instance.getCreatedTime().format(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG))));
        add(new Paragraph("Running build #" + instance.getBuildId() +
                              " (image " + instance.getApplicationImage() + ")"));
        add(new ExplorVizControls(instance, stopListener));
    }
}
