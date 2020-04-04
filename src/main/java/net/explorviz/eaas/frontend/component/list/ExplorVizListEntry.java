package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.icon.VaadinIcon;
import net.explorviz.eaas.frontend.component.InstanceControls;
import net.explorviz.eaas.model.entity.Project;
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

        Project project = instance.getBuild().getProject();
        String projectText = "";
        // Dirty hack for incomplete DummyBuild implementation in GlobalInstancesView
        if (project != null) {
            projectText = " of Project #" + project.getId() + " ('" + project.getName() + "')";
        }

        getElement().appendChild(
            createParagraph("ExplorViz " + instance.getVersion() + ", started " +
                instance.getStartedTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG))),
            createParagraph("Running Build #" + instance.getBuild().getId() +
                " ('" + instance.getBuild().getName() + "')" + projectText),
            createParagraph("Image " + instance.getBuild().getDockerImage())
        );

        add(new InstanceControls(instance, manager, dockerCompose, stopCallback));
    }
}
