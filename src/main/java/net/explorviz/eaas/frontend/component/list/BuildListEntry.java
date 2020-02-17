package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.icon.VaadinIcon;
import net.explorviz.eaas.frontend.component.InstanceControls;
import net.explorviz.eaas.frontend.component.BuildControls;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.security.Authorities;
import net.explorviz.eaas.security.SecurityUtils;
import net.explorviz.eaas.service.explorviz.ExplorVizInstance;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import static com.vaadin.flow.dom.ElementFactory.createParagraph;

/**
 * Represents a single build in a {@link RichList}, displaying the appropriate controls whether there is already an
 * {@link ExplorVizInstance} running for this build or not.
 */
public class BuildListEntry extends AbstractListEntry {
    private static final long serialVersionUID = -370968210512960640L;

    private final Build build;
    private final ExplorVizManager manager;

    private final RichHeader header;

    public BuildListEntry(Build build, ExplorVizManager manager) {
        this.build = build;
        this.manager = manager;

        header = RichHeader.create(VaadinIcon.CHEVRON_CIRCLE_RIGHT.create(), build.getName());

        build();
    }

    private void rebuild() {
        removeAll();
        build();
    }

    private void build() {
        add(header);

        Optional<ExplorVizInstance> instance = manager.getInstance(build);
        header.getIcon().setVisible(instance.isPresent());

        getElement().appendChild(
            createParagraph("Added " + build.getCreatedDate().format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG))),
            createParagraph("Image ID: " + build.getDockerImage())
        );

        if (SecurityUtils.hasAuthority(Authorities.RUN_BUILD_AUTHORITY)) {
            if (instance.isPresent()) {
                add(new InstanceControls(instance.get(), manager, ignored -> this.rebuild()));
            } else {
                add(new BuildControls(build, manager, ignored -> this.rebuild()));
            }
        }
    }
}
