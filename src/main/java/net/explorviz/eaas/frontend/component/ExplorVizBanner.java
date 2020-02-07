package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import net.explorviz.eaas.Application;

@CssImport("explorviz-banner.css")
public class ExplorVizBanner extends HorizontalLayout {
    private static final long serialVersionUID = 3269187260966648734L;

    private static final String LOGO_SIZE_BIG = "96px";
    private static final String LOGO_SIZE_SMALL = "48px";

    /**
     * @param big {@code true} if the banner is displayed big, e.g. on the login page
     */
    public ExplorVizBanner(boolean big) {
        setId("explorviz-banner");

        Image img = new Image("/icons/icon-192x192.png", "ExplorViz");
        img.setHeight(big ? LOGO_SIZE_BIG : LOGO_SIZE_SMALL);
        img.setWidth(big ? LOGO_SIZE_BIG : LOGO_SIZE_SMALL);
        add(img);

        add(big ? new H2(Application.PAGE_TITLE) : new H3(Application.PAGE_TITLE));

        setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
    }
}
