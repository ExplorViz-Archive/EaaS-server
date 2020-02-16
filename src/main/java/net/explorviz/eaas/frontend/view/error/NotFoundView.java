package net.explorviz.eaas.frontend.view.error;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.NotFoundException;

import javax.servlet.http.HttpServletResponse;

public class NotFoundView extends AbstractErrorView<NotFoundException> {
    private static final long serialVersionUID = 7325472405508806255L;

    public NotFoundView() {
        super(HttpServletResponse.SC_NOT_FOUND, "Not Found", "Page not found", VaadinIcon.FILE_SEARCH.create());
    }
}
