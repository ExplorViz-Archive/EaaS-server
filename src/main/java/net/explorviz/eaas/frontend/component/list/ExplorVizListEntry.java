package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import net.explorviz.eaas.frontend.component.InstanceControls;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.function.Consumer;

public class ExplorVizListEntry extends SimpleListEntry {
    private static final long serialVersionUID = -1493130701688498902L;

    /**
     * @see InstanceControls#InstanceControls(ExplorVizInstance, ExplorVizManager, Consumer)
     */
    public ExplorVizListEntry(ExplorVizInstance instance, ExplorVizManager manager,
                              Consumer<? super ExplorVizInstance> stopCallback) {
        add(new H4(instance.getName()));
        add(new Paragraph("ExplorViz " + instance.getVersion() + ", started " + instance.getStartedTime().format(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG))));
        add(new Paragraph("Running build #" + instance.getBuild().getId() +
            " (image " + instance.getBuild().getDockerImage() + ")"));
        add(new InstanceControls(instance, manager, stopCallback));
    }
}
