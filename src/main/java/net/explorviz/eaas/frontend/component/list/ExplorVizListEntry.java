package net.explorviz.eaas.frontend.component.list;

import net.explorviz.eaas.frontend.component.InstanceControls;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.function.Consumer;

import static com.vaadin.flow.dom.ElementFactory.createHeading4;
import static com.vaadin.flow.dom.ElementFactory.createParagraph;

public class ExplorVizListEntry extends SimpleListEntry {
    private static final long serialVersionUID = -1493130701688498902L;

    /**
     * @see InstanceControls#InstanceControls(ExplorVizInstance, ExplorVizManager, Consumer)
     */
    public ExplorVizListEntry(ExplorVizInstance instance, ExplorVizManager manager,
                              Consumer<? super ExplorVizInstance> stopCallback) {
        getElement().appendChild(
            createHeading4(instance.getName()),
            createParagraph("ExplorViz " + instance.getVersion() + ", started " +
                instance.getStartedTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG))),
            createParagraph("Running build #" + instance.getBuild().getId() +
                " (image " + instance.getBuild().getDockerImage() + ")")
        );

        add(new InstanceControls(instance, manager, stopCallback));
    }
}
