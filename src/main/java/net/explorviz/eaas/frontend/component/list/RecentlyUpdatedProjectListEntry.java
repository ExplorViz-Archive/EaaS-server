package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.icon.VaadinIcon;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.repository.RecentlyUpdatedResult;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static com.vaadin.flow.dom.ElementFactory.createParagraph;

public class RecentlyUpdatedProjectListEntry extends AbstractListEntry {
    private static final long serialVersionUID = 1589947241429733513L;

    public RecentlyUpdatedProjectListEntry(RecentlyUpdatedResult recentlyUpdatedResult) {
        Project project = recentlyUpdatedResult.getProject();
        add(new RichLinkHeader(BuildsView.class, project.getId(), VaadinIcon.ARCHIVE.create(), project.getName()));

        Build build = recentlyUpdatedResult.getBuild();
        if (build != null) {
            getElement().appendChild(
                createParagraph("#" + build.getId() + " " + build.getName()),
                createParagraph("Added " + build.getCreatedDate().format(
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
                ))
            );
        } else {
            getElement().appendChild(createParagraph("No builds added yet"));
        }
    }
}
