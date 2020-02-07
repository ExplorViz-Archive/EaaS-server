package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import net.explorviz.eaas.Application;

@CssImport("explorviz-banner.css")
public class ExplorVizBanner extends HorizontalLayout {
    private static final long serialVersionUID = 3269187260966648734L;

    /**
     * @param big {@code true} if the banner is displayed big, e.g. on the login page
     */
    public ExplorVizBanner(boolean big) {
        setId("explorviz-banner");
        addClassName("explorviz-banner-" + (big ? "big" : "small"));

        add(new Image("/icons/icon-192x192.png", "ExplorViz"));
        add(new H2(Application.PAGE_TITLE));

        setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
    }
}
