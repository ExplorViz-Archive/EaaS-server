package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.model.entity.Project;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ProjectListEntry extends SimpleListEntry {
    private static final long serialVersionUID = 8271392331540142853L;

    public ProjectListEntry(Project project) {
        add(new SimpleListLink(BuildsView.class, project.getId(), VaadinIcon.ARCHIVE, project.getName()));

        add(new Paragraph("Created " + project.getCreatedDate().format(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
        )));
    }
}
