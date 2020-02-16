package net.explorviz.eaas.frontend.view.error;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.layout.BaseLayout;

import static com.vaadin.flow.dom.ElementFactory.createHeading1;
import static com.vaadin.flow.dom.ElementFactory.createParagraph;

@Tag("sa-error-view")
@CssImport("./style/error-view.css")
@ParentLayout(BaseLayout.class)
public abstract class AbstractErrorView<T extends Exception> extends VerticalLayout implements HasErrorParameter<T>,
    HasDynamicTitle {
    private static final long serialVersionUID = 5249333449652486909L;

    private final int httpCode;
    private final String errorName;

    private String message;

    /**
     * @param httpCode       HTTP Error code from {@link javax.servlet.http.HttpServletResponse} for the client
     * @param errorName      Name of the error that will be displayed as page title
     * @param defaultMessage Default message to display if the error
     * @param icon           Icon to display, should go well with the given {@code errorName}
     */
    protected AbstractErrorView(int httpCode, String errorName, String defaultMessage, Icon icon) {
        this.httpCode = httpCode;
        this.errorName = errorName;

        this.message = defaultMessage;

        addClassName("centered-view");

        add(icon);
        getElement().appendChild(createHeading1(errorName));
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<T> parameter) {
        if (parameter.hasCustomMessage()) {
            this.message = parameter.getCustomMessage();
        }

        getElement().appendChild(createParagraph(message));
        return httpCode;
    }

    @Override
    public String getPageTitle() {
        return errorName + " - " + Application.PAGE_TITLE;
    }
}
