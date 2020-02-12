package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.repository.RecentlyUpdatedResult;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class RecentlyUpdatedProjectListEntry extends SimpleListEntry {
    private static final long serialVersionUID = 1589947241429733513L;

    public RecentlyUpdatedProjectListEntry(RecentlyUpdatedResult recentlyUpdatedResult) {
        Project project = recentlyUpdatedResult.getProject();
        add(new SimpleListLink(BuildsView.class, project.getId(), VaadinIcon.ARCHIVE, project.getName()));

        Build build = recentlyUpdatedResult.getBuild();
        if (build != null) {
            add(new Paragraph("#" + build.getId() + " " + build.getName()));
            add(new Paragraph("Added " + build.getCreatedDate().format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
            )));
        } else {
            add(new Paragraph("No builds added yet"));
        }
    }
}
