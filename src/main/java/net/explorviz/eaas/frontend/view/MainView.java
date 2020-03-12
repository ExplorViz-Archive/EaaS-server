package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.component.list.RecentlyUpdatedListEntry;
import net.explorviz.eaas.frontend.component.list.RichList;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.model.repository.BuildRepository;
import net.explorviz.eaas.security.SecurityUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;
import static com.vaadin.flow.dom.ElementFactory.createParagraph;

@PageTitle(Application.PAGE_TITLE)
@Route(value = "", layout = MainLayout.class)
public class MainView extends DynamicView {
    private static final long serialVersionUID = -4417018497155730464L;

    private final BuildRepository buildRepo;
    private final int entriesPerPage;

    public MainView(BuildRepository buildRepo,
                    @Value("${eaas.paging.home.entries}") int entriesPerPage) {
        Validate.inclusiveBetween(1, Integer.MAX_VALUE, entriesPerPage, "entries must be at least 1");

        this.buildRepo = buildRepo;
        this.entriesPerPage = entriesPerPage;
    }

    @Override
    protected void build() {
        getElement().appendChild(createHeading2("Recently updated projects"));

        Page<Build> mostRecentBuilds = buildRepo.findMostRecentBuilds(false,
            SecurityUtils.getCurrentUser().orElse(null), PageRequest.of(0, entriesPerPage));

        if (mostRecentBuilds.getTotalElements() == 0) {
            getElement().appendChild(createParagraph("No builds have been uploaded yet."));
        } else {
            RichList<Build> recentBuildList = new RichList<>(RecentlyUpdatedListEntry::new);
            recentBuildList.addEntries(mostRecentBuilds);
            add(recentBuildList);
        }
    }
}
