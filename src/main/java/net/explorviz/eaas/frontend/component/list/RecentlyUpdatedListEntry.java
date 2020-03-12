package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.icon.VaadinIcon;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.model.entity.Build;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static com.vaadin.flow.dom.ElementFactory.createParagraph;

public class RecentlyUpdatedListEntry extends AbstractListEntry {
    private static final long serialVersionUID = 3590351523668425287L;

    public RecentlyUpdatedListEntry(Build build) {
        add(RichHeader.createWithLink(BuildsView.class, build.getProject().getId(),
            VaadinIcon.CHEVRON_CIRCLE_RIGHT.create(), build.getProject().getName()));
        getElement().appendChild(createParagraph(build.getName()),
            createParagraph("Added " + build.getCreatedDate().format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG))));
    }
}
