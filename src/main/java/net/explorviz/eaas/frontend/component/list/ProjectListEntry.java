package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.icon.VaadinIcon;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.model.entity.Project;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static com.vaadin.flow.dom.ElementFactory.createParagraph;

public class ProjectListEntry extends AbstractListEntry {
    private static final long serialVersionUID = 8271392331540142853L;

    public ProjectListEntry(Project project) {
        add(new RichLinkHeader(BuildsView.class, project.getId(), VaadinIcon.ARCHIVE.create(), project.getName()));

        getElement().appendChild(createParagraph("Created " + project.getCreatedDate().format(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
        )));
    }
}
