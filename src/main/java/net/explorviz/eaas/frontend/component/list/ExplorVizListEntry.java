package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.icon.VaadinIcon;
import net.explorviz.eaas.frontend.component.InstanceControls;
import net.explorviz.eaas.service.docker.compose.DockerComposeAdapter;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.function.Consumer;

import static com.vaadin.flow.dom.ElementFactory.createParagraph;

public class ExplorVizListEntry extends AbstractListEntry {
    private static final long serialVersionUID = -1493130701688498902L;

    /**
     * @see InstanceControls#InstanceControls(ExplorVizInstance, ExplorVizManager, DockerComposeAdapter, Consumer)
     */
    public ExplorVizListEntry(ExplorVizInstance instance, ExplorVizManager manager, DockerComposeAdapter dockerCompose,
                              Consumer<? super ExplorVizInstance> stopCallback) {
        add(RichHeader.create(VaadinIcon.CHEVRON_CIRCLE_RIGHT.create(), instance.getName()));

        getElement().appendChild(
            createParagraph("ExplorViz " + instance.getVersion() + ", started " +
                instance.getStartedTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG))),
            createParagraph("Running build #" + instance.getBuild().getId() +
                " (image " + instance.getBuild().getDockerImage() + ")")
        );

        add(new InstanceControls(instance, manager, dockerCompose, stopCallback));
    }
}
