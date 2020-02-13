package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import net.explorviz.eaas.frontend.component.InstanceControls;
import net.explorviz.eaas.frontend.component.RunBuildControls;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

/**
 * Represents a single build in a {@link SimpleList}, displaying the appropriate controls whether there is already an
 * {@link ExplorVizInstance} running for this build or not.
 */
public class BuildListEntry extends SimpleListEntry {
    private static final long serialVersionUID = -370968210512960640L;

    private final Build build;
    private final ExplorVizManager manager;

    private final HorizontalLayout header;
    private final Icon runningIcon;

    public BuildListEntry(Build build, ExplorVizManager manager) {
        this.build = build;
        this.manager = manager;
        this.runningIcon = VaadinIcon.CHEVRON_CIRCLE_RIGHT.create();

        header = new HorizontalLayout();
        header.addClassName("simple-list-header");
        header.add(runningIcon);
        header.add(new H4(build.getName()));

        build();
    }

    private void rebuild() {
        removeAll();
        build();
    }

    private void build() {
        add(header);

        Optional<ExplorVizInstance> instance = manager.getInstance(build);
        runningIcon.setVisible(instance.isPresent());

        add(new Paragraph("Added " + build.getCreatedDate().format(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG))));
        add(new Paragraph("Image ID: " + build.getDockerImage()));

        if (instance.isPresent()) {
            add(new InstanceControls(instance.get(), manager, ignored -> this.rebuild()));
        } else {
            add(new RunBuildControls(build, manager, ignored -> this.rebuild()));
        }
    }
}
